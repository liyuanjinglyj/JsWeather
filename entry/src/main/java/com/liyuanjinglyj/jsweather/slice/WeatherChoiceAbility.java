package com.liyuanjinglyj.jsweather.slice;

import com.liyuanjinglyj.jsweather.ResourceTable;
import com.liyuanjinglyj.jsweather.common.WeatherImageMap;
import com.liyuanjinglyj.jsweather.utils.LYJUtils;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.FormBindingData;
import ohos.aafwk.ability.FormException;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.TextField;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.zson.ZSONArray;
import ohos.utils.zson.ZSONObject;

import java.util.Map;

public class WeatherChoiceAbility extends Ability {
    private static final HiLogLabel TAG = new HiLogLabel(HiLog.DEBUG, 0x0, WeatherChoiceAbility.class.getName());
    private TextField textField;
    private Button button;
    private String url="https://yiketianqi.com/api?version=v9&appid=73913812&appsecret=458XM5hq&city=";
    private long formId;
    private String city;

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_weather_choice_slice);
        this.textField=(TextField)findComponentById(ResourceTable.Id_ability_weather_choice_textfield);
        this.button=(Button)findComponentById(ResourceTable.Id_ability_weather_choice_button);
        formId=intent.getLongParam(AbilitySlice.PARAM_FORM_IDENTITY_KEY, -1);
        this.button.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                TaskDispatcher refreshUITask = createParallelTaskDispatcher("", TaskPriority.DEFAULT);
                refreshUITask.syncDispatch(()->{
                    ZSONArray zsonArray=getData(textField.getText().trim());
                    HiLog.error(TAG,zsonArray.toString());

                    try {
                        ZSONObject object=new ZSONObject();
                        object.put("itemTitle",city);
                        object.put("weatherList",zsonArray);
                        FormBindingData bindingData = new FormBindingData(object);
                        if(!updateForm(formId,bindingData)){
                            HiLog.error(TAG,"updateForm error");
                        }else{
                            terminateAbility();
                        }
                    }catch (Exception e){
                        HiLog.error(TAG,"updateForm error");
                    }
                });
            }
        });
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
        ZSONObject list_data = (ZSONObject)data.get(0);
        String content=list_data.getString("date");
        return weatherList;
    }
}
