import fetch from '@system.fetch';

export default {
    data: {
        responseData: ""
    },
    onInit() {
        var that = this;
        fetch.fetch({
            url: "https://yiketianqi.com/api?version=v9&appid=73913812&appsecret=458XM5hq&city=北京",
            success: function (response) {
                console.info("网络请求成功");
                that.responseData = JSON.parse(response.data);
                that.responseData = that.responseData.data[0].date
            },
            fail: function () {
                console.info("网络请求错误");
            }
        });
    }
}
