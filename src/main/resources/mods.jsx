class App extends React.Component {

    constructor() {
        super();
        this.state = {mods: []}
    }

    componentDidMount() {
        $.ajax({
            url: `/${this.mode()}`,
            type: "GET",
            success: (res) => {
                this.setState({mods: res})
            }
        })
    }

    // noinspection JSMethodCanBeStatic
    renderHead() {
        let ths = ["重复", "序号", "编号", "名称", "list", "data", "操作"].map((item, idx) => {
            return <th key={idx}>{item}</th>
        });
        return <tr>{ths}</tr>
    }

    mode() {
        let filter = _.fromPairs((location.search || "").slice(1).split("&").map(s => s.split("=")));
        return filter.mode || "mods"
    }

    renderBody() {
        let mods = this.state.mods;
        // noinspection UnnecessaryLocalVariableJS
        let body = mods.map((mod, idx) => {
            let onChange = (e) => {
                let mods = this.state.mods.map((m, i) => {
                    if (idx === i) {
                        return _.defaults({_no: e.target.value}, m)
                    } else {
                        return m
                    }
                });
                this.setState({mods})
            };
            let onSubmitChangeNo = () => {
                if (mod.no.length !== mod._no.length) {
                    throw  Error("Length Not Match")
                }
                $.ajax({
                    url: `/list/${mod.list}?from=${mod.no}&to=${this.state.mods[idx]._no}`,
                    type: "PUT",
                    success: () => {
                        let state = _.cloneDeep(this.state);
                        state.mods[idx].no = state.mods[idx]._no;
                        state.mods[idx]._no = "";
                        this.setState(state)
                    },
                    error: (err) => {
                        alert(err.responseText)
                    }
                })
            };
            let onSubmitCopyMod = () => {

            };
            let onSubmit = {
                "mods": onSubmitChangeNo,
                "diff": onSubmitCopyMod,
            }[this.mode()];
            let dup = <td key="dup">
                {idx > 0 && mod.no === mods[idx - 1].no && mod.name !== mods[idx - 1].name ? "<+>" : ""}
            </td>;
            let seq = <td key="seq">{idx}</td>;
            let tds = ["no", "name", "list", "data"].map(name => {
                return <td key={name}>{mod[name]}</td>
            });
            let op = <td key="op">
                <input type="text" value={mod._no || ""} onChange={onChange}/>
                <a onClick={onSubmit}>修改</a>
            </td>;
            return <tr key={idx}>{_.flatten([dup, seq, tds, op])}</tr>
        });
        return body;
    }

    renderSelected() {
        let selected = this.state.mods.filter(m => !!m._no);
        let onClick = () => {
            let data = selected.map(m => {
                return [m.list, m.no, m._no]
            });
            $.ajax({
                url: "/list",
                type: "PUT",
                data: JSON.stringify(data),
                success: () => {
                    let mods = this.state.mods.map(m => {
                        if (!m._no) {
                            return m
                        } else {
                            return _.defaults({no: m._no, _no: ""}, m)
                        }
                    });
                    this.setState({mods})
                }
            })
        };
        return <div>
            <table className="table">
                <tbody>
                {selected.map(mod => {
                    return <tr>{["no", "name", "list", "data", "_no"].map(name => {
                        return <td key={name}>{mod[name]}</td>
                    })}</tr>
                })}
                </tbody>
            </table>
            <button className="btn btn-primary" onClick={onClick}>确认</button>
        </div>
    }

    render() {
        return <div>
            <div>
                <a href="/?mode=mods">mods</a>
                <a href="/?mode=diff">diff</a>
            </div>
            {this.renderSelected()}
            <table className="table">
                <thead>
                {this.renderHead()}
                </thead>
                <tbody>
                {this.renderBody()}
                </tbody>
            </table>
        </div>
    }
}

$.ajaxSetup({contentType: "application/json; charset=utf-8"});
ReactDOM.render(<App/>, document.getElementById("root"));

