let initData = (() => {
    //  noinspection UnnecessaryReturnStatementJS
    return //$INIT_DATA
})();

class App extends React.Component {
    render() {
        return <h1>hello App</h1>
    }
}

ReactDOM.render(<App/>, document.getElementById("root"));

