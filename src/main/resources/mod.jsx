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
        let list = tr.find(".list").text();
        let from = tr.find(".no").text();
        let to = tr.find(".op>input").val();
        if (!check(to)) {
            return;
        }
        $.ajax({
            url: `/list/${list}?from=${from}&to=${to}`,
            type: "PUT",
            data: "{}",
            success: () => {
                tr.find(".dup").text("");
                tr.find(".no").text(to);
                tr.find(".op>input").val("");
            }
        })
    });
    let needChange = [];
    $("#refresh").unbind().click(function () {
        needChange = [];
        $("tr").each(function () {
            let tr = $(this);
            let list = tr.find(".list").text();
            let from = tr.find(".no").text();
            let to = tr.find(".op>input").val();
            if (to.length > 5 && check(to)) {
                needChange.push({list, from, to})
            }
        });
        console.log(needChange);
        let text = needChange.map(m => {
            return `<div>${m.list} ${m.from} ${m.to}</div>`
        }).join("");
        $("#need-change").html(text)
    });
    $("#submit").unbind().click(function () {
        if (!needChange.length) {
            alert("没有选中的");
            return;
        }
        $.ajax({
            url: `/list`,
            type: "PUT",
            data: JSON.stringify(needChange),
            success: () => {
                needChange = [];
                $("#need-change").html("")
            }
        })
    })
});