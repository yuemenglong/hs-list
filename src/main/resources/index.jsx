class App extends React.Component {

    constructor() {
        super();
        this.state = {mods: []}
    }

    componentDidMount() {
        $.ajax({
            url: "/mods",
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

    renderBody() {
        let mods = _.sortBy(this.state.mods, m => m.no);
        // noinspection UnnecessaryLocalVariableJS
        let body = mods.map((mod, idx) => {
            let onChange = (e) => {
                let state = _.cloneDeep(this.state);
                state.mods[idx]._no = e.target.value;
                this.setState(state)
            };
            let onSubmit = () => {
                $.ajax({
                    url: `/mod?list=${mod.list}&no=${mod.no}`,
                    type: "PUT",
                    data: JSON.stringify({no: this.state.mods[idx]._no}),
                    success: () => {
                    },
                    error: (err) => {
                        alert(err.responseText)
                    }
                })
            };
            let dup = <td key="dup">
                {idx > 0 && mod.no === mods[idx - 1].no && mod.name !== mods[idx - 1].name ? "<+>" : ""}
            </td>;
            let seq = <td key="seq">{idx}</td>;
            let tds = ["no", "name", "list", "data"].map(name => {
                return <td key={name}>{mod[name]}</td>
            });
            let op = <td key="op">
                <input type="text" onChange={onChange}/>
                <a onClick={onSubmit}>修改</a>
            </td>;
            return <tr key={idx}>{_.flatten([dup, seq, tds, op])}</tr>
        });
        return body;
    }

    render() {
        return <div>
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

