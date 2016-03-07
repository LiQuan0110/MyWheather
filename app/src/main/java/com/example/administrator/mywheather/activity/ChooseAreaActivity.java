package com.example.administrator.mywheather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.mywheather.R;
import com.example.administrator.mywheather.db.MyWeatherDB;
import com.example.administrator.mywheather.model.City;
import com.example.administrator.mywheather.model.County;
import com.example.administrator.mywheather.model.Province;
import com.example.administrator.mywheather.util.HttpCallbackListener;
import com.example.administrator.mywheather.util.HttpUtil;
import com.example.administrator.mywheather.util.Utility;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/6.
 */
//遍历省市县数据的活动
public class ChooseAreaActivity extends Activity{
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY= 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String>adapter;
    private MyWeatherDB myWeatherDB;
    private List<String> dataList = new ArrayList<>();

    //省列表
    private List<Province> provinceList;
    //市列表
    private List<City> cityList;
    //县列表
    private List<County> countyList;

    //选中的省份
    private Province selectedProvince;
    //选中的城市
    private City selectedCity;
    //当前选中的级别
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        myWeatherDB =  MyWeatherDB.getInstance(this);//?？？
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //监听子条目的点击事件
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position); //??
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        queryProvinces();  //加载省级数据
    }

    //查询全国所有的省，优先从数据库查询，如果没有再去服务器查询
    private void queryProvinces(){
        provinceList = myWeatherDB.loadProvinces();
        if (provinceList.size()>0){
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();  //动态刷新适配器的内容
            listView.setSelection(0);//将列表移动到指定的位置，定位某一条数据
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else{
            queryFromServer(null,"province");
        }
    }

    //查询选中省内所有的市，优先从数据库查询，如果没有再去服务器查询
    private void queryCities(){
        cityList = myWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size()>0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else{
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }

    //查询选中市内所有县，优先从数据库查询，没有就去服务器查询
    private void queryCounties(){
        if (countyList.size() > 0){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel=LEVEL_COUNTY;
        }else{
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }

    //根据传入的代号和类型从服务器上查询省市县数据
    private void queryFromServer(final String code,final String type){
        String address;
        if (!TextUtils.isEmpty(code)){  //判断code字符串是否为空，再判断长度是否为0
            address = "http://www.weather.com.cn/data/list3/city" + code+".xml";
        }else{
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvincesResponse(myWeatherDB,response);
                }else if ("city".equals(type)){
                    result = Utility.handleCityResponse(myWeatherDB,response,selectedProvince.getId());
                }else if ("county".equals(type)){
                    result = Utility.handleCityResponse(myWeatherDB,response,selectedCity.getId());
                }

                if (result){
                    //通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {  //更新Ui
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                //通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //显示进度对话框
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载.....");
            progressDialog.setCanceledOnTouchOutside(false);//在对话框的外面点击，是否让对话框消失
        }
        progressDialog.show();
    }
    //关闭进度对话框
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    //根据back按键，根据当前的级别来判断，这时应该返回市列表，省列表，还是直接退出

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (currentLevel == LEVEL_COUNTY){
            queryCities();
        }else if (currentLevel == LEVEL_CITY){
            queryProvinces();
        }else{
            finish();
        }
    }
}
