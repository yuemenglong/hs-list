$(() => {
    function check(to) {
        if (!/\d{6,9}/.test(to)) {
            alert("格式不正确");
            return false;
        }
        return true;
    }

    $("td.op>a").unbind();
    $("td.op>a").click(function () {
        let tr = $(this).parent().parent();
        let name = $(tr.children()[4]).text();
        let from = $(tr.children()[2]).text();
        let to = $(tr.find(".op>input")[0]).val();
        if (!check(to)) {
            return;
        }
        $.ajax({
            url: `/list/${name}?from=${from}&to=${to}`,
            type: "PUT",
            data: "{}",
            success: () => {
                $(tr.children()[0]).text("");
                $(tr.children()[2]).text(to);
                $(tr.find(".op>input")[0]).val("");
            }
        })
    });
    let needChange = [];
    $("#refresh").unbind().click(function () {
        needChange = [];
        $("tr").each(function () {
            let tr = $(this);
            let name = $(tr.children()[4]).text();
            let from = $(tr.children()[2]).text();
            let to = $(tr.find(".op>input")[0]).val();
            if (to.length > 5 && check(to)) {
                needChange.push({name, from, to})
            }
        });
        console.log(needChange);
        let text = needChange.map(m => {
            return `<div>${m.name} ${m.from} ${m.to}</div>`
        }).join("");
        $("#need-change").html(text)
    });
    $("#submit").unbind().click(function () {
        $("#need-change").html("")
    })
});