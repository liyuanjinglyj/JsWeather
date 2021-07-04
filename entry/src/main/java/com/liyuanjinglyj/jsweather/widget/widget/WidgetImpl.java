package com.liyuanjinglyj.jsweather.widget.widget;

import com.liyuanjinglyj.jsweather.common.WeatherImageMap;
import com.liyuanjinglyj.jsweather.utils.LYJUtils;
import com.liyuanjinglyj.jsweather.widget.controller.FormController;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.FormBindingData;
import ohos.aafwk.ability.FormException;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.aafwk.content.Intent;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.zson.ZSONArray;
import ohos.utils.zson.ZSONObject;
import java.util.Map;

/**
 * The WidgetImpl
 */
public class WidgetImpl extends FormController {
    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, WidgetImpl.class.getName());
    private final Context mContext;
    private String url="https://yiketianqi.com/api?version=v9&appid=73913812&appsecret=458XM5hq&city=";
    private String city;
    public WidgetImpl(Context context, String formName, Integer dimension) {
        super(context, formName, dimension);
        mContext = context;
    }

    private ZSONArray getData(String cityStr){
        LYJUtils http = new LYJUtils();
        String response = http.doGet(url+cityStr);
        ZSONObject res = ZSONObject.stringToZSON(response);
        city = res.getString("city");
        HiLog.info(TAG, city);
        ZSONArray data = res.getZSONArray("data");
        WeatherImageMap weatherImageMap=new WeatherImageMap();
        Map<String,String> map= weatherImageMap.getMap();
        ZSONArray weatherList=new ZSONArray();
        for(int i=0;i<data.size();i++){
            ZSONObject zsonObject = (ZSONObject)data.get(i);
            ZSONObject newObject=new ZSONObject();
            newObject.put("image",map.get(zsonObject.getString("wea_day")));
            newObject.put("wea_day",zsonObject.get("wea_day"));
            newObject.put("week",zsonObject.get("week"));
            weatherList.add(newObject);
        }
        return weatherList;
    }

    //卡片初始化时方法
    @Override
    public ProviderFormInfo bindFormData() {
        HiLog.info(TAG, "开始");
        ZSONObject object = new ZSONObject();
        object.put("weatherList", getData("北京"));
        object.put("itemTitle", city);
        FormBindingData bindingData = new FormBindingData(object);
        ProviderFormInfo formInfo = new ProviderFormInfo();
        formInfo.setJsBindingData(bindingData);
        return formInfo;
    }

    @Override
    public void updateFormData(long formId, Object... vars) {
    }

    // 卡片消息事件时方法
    @Override
    public void onTriggerFormEvent(long formId, String message) {
        ZSONObject zsonObject = ZSONObject.stringToZSON(message);
        HiLog.info(TAG, "onTriggerFormEvent");
        // Do something here after receive the message from js card
        ZSONObject result = new ZSONObject();
        switch (zsonObject.getString("mAction")) {
            case "refresh":
                result.put("weatherList",getData("宜昌"));
                break;
            default:
                break;
        }

        // Update js card
        try {
            if (mContext instanceof Ability) {
                ((Ability) mContext).updateForm(formId, new FormBindingData(result));
            }
        } catch (FormException e) {
            HiLog.error(TAG, e.getMessage());
        }
    }

    @Override
    public Class<? extends AbilitySlice> getRoutePageSlice(Intent intent) {
        return null;
    }
}


/**
 *
 final FormBindingData[] bindingData = new FormBindingData[1];
 ZSONObject object = new ZSONObject();
 ZZRHttp.get(url, new ZZRCallBack.CallBackString() {
@Override
public void onFailure(int code, String errorMessage) {
//http访问出错了，此部分内容在主线程中工作;
//可以更新UI等操作,请不要执行阻塞操作。
}
@Override
public void onResponse(String response) {
//http访问成功，此部分内容在主线程中工作;
//可以更新UI等操作，但请不要执行阻塞操作。
HiJson hiJson = new HiJson(response);
int counts=hiJson.get("data").count();
List<LYJWeather> lyjWeatherList=new ArrayList<>();
for(int i=0;i<counts;i++){
LYJWeather lyjWeather=new LYJWeather();
lyjWeather.setDate(hiJson.get("data").get(i).value("date"));
lyjWeather.setWea(hiJson.get("data").get(i).value("wea"));
lyjWeather.setTem(hiJson.get("data").get(i).value("tem"));
lyjWeather.setTem1(hiJson.get("data").get(i).value("tem1"));
lyjWeather.setTem2(hiJson.get("data").get(i).value("tem2"));
lyjWeatherList.add(lyjWeather);
}
object.put("content", lyjWeatherList.get(3).getDate());
HiLog.info(TAG, "message type: " + lyjWeatherList.get(3).getDate());
bindingData[0] =new FormBindingData(object);
}
});
 return bindingData[0];
 * */