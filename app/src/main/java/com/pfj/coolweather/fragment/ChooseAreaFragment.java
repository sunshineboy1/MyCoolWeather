package com.pfj.coolweather.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pfj.coolweather.R;
import com.pfj.coolweather.db.City;
import com.pfj.coolweather.db.County;
import com.pfj.coolweather.db.Province;
import com.pfj.coolweather.util.HttpUtil;
import com.pfj.coolweather.util.LogUtil;
import com.pfj.coolweather.util.ToastUtil;
import com.pfj.coolweather.util.Utility;

import org.jetbrains.annotations.NotNull;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment implements
        View.OnClickListener, AdapterView.OnItemClickListener {

    private TextView tvTitle;
    private ImageView imageView;
    private ListView listView;
    private List<String> datas = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    //
    public static final int PROVINCE = 0;
    public static final int CITY = 1;
    public static final int COUNTY = 2;
    public static int current_level;

    //
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    //点击选中的省或者市
    private Province selectProvince;
    private City selectCity;

    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        imageView = view.findViewById(R.id.backView);
        tvTitle = view.findViewById(R.id.tvName);
        listView = view.findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, datas);
        listView.setAdapter(arrayAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageView.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        queryProvince();
    }

    /**
     * 所以省
     */
    private void queryProvince() {
        tvTitle.setText("中国");
        imageView.setVisibility(View.INVISIBLE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            datas.clear();
            for (Province province : provinceList) {
                datas.add(province.provinceName);
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            current_level = PROVINCE;
        } else {
            queryFromServer("http://guolin.tech/api/china", "Province");
        }
    }

    /**
     * 选中省的所有市
     */
    private void queryCity() {
        tvTitle.setText(selectProvince.provinceName);
        imageView.setVisibility(View.VISIBLE);
        //所有 省下的城市
        cityList = DataSupport.where("provinceid=?", String.valueOf(selectProvince.id)).find(City.class);
        if (cityList.size() > 0) {
            datas.clear();
            for (City city : cityList) {
                datas.add(city.cityName);
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            current_level = CITY;
        } else {
            queryFromServer("http://guolin.tech/api/china/" + selectProvince.provinceCode, "City");
        }
    }

    /**
     * 选中市的所有县
     */
    private void queryCounty() {
        tvTitle.setText(selectCity.cityName);
        imageView.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid=?", String.valueOf(selectCity.id)).find(County.class);
        if (countyList.size() > 0) {
            datas.clear();
            for (County county : countyList) {
                datas.add(county.countyName);
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            current_level = COUNTY;
        } else {
            String url = "http://guolin.tech/api/china/" + selectProvince.provinceCode + "/" + selectCity.cityCode;
            queryFromServer(url, "County");
        }
    }

    //从服务器获取数据
    private void queryFromServer(String url, final String level) {
        showProgressDialig();
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtil.e(e.getMessage());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        ToastUtil.shortToast("服务器获取数据失败！！！");
                    }
                });
//                String response = "[{\"id\":1,\"name\":\"北京\"},{\"id\":2,\"name\":\"上海\"},{\"id\":3,\"name\":\"天津\"},{\"id\":4,\"name\":\"重庆\"},{\"id\":5,\"name\":\"香港\"},{\"id\":6,\"name\":\"澳门\"},{\"id\":7,\"name\":\"台湾\"},{\"id\":8,\"name\":\"黑龙江\"},{\"id\":9,\"name\":\"吉林\"},{\"id\":10,\"name\":\"辽宁\"},{\"id\":11,\"name\":\"内蒙古\"},{\"id\":12,\"name\":\"河北\"},{\"id\":13,\"name\":\"河南\"},{\"id\":14,\"name\":\"山西\"},{\"id\":15,\"name\":\"山东\"},{\"id\":16,\"name\":\"江苏\"},{\"id\":17,\"name\":\"浙江\"},{\"id\":18,\"name\":\"福建\"},{\"id\":19,\"name\":\"江西\"},{\"id\":20,\"name\":\"安徽\"},{\"id\":21,\"name\":\"湖北\"},{\"id\":22,\"name\":\"湖南\"},{\"id\":23,\"name\":\"广东\"},{\"id\":24,\"name\":\"广西\"},{\"id\":25,\"name\":\"海南\"},{\"id\":26,\"name\":\"贵州\"},{\"id\":27,\"name\":\"云南\"},{\"id\":28,\"name\":\"四川\"},{\"id\":29,\"name\":\"西藏\"},{\"id\":30,\"name\":\"陕西\"},{\"id\":31,\"name\":\"宁夏\"},{\"id\":32,\"name\":\"甘肃\"},{\"id\":33,\"name\":\"青海\"},{\"id\":34,\"name\":\"新疆\"}]";
//                handlResponse(level,response);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = response.body().string();
                handlResponse(level,res);

            }
        });
    }

    private void handlResponse(final String level,String res){
        boolean bRes = false;
        /**
         * 数据存入DB
         */
        if (level.equals("Province")) {
            bRes = Utility.handleProvince(res);
        } else if ("City".equals(level)) {
            bRes = Utility.handleCity(res, selectProvince.id);
        } else if ("County".equals(level)) {
            bRes = Utility.handleCounty(res, selectCity.id);
        }
        if (bRes) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    closeProgressDialog();
                    /**
                     * 从DB查询数据
                     */
                    if (level.equals("Province")) {
                        queryProvince();
                    } else if ("City".equals(level)) {
                        queryCity();
                    } else if ("County".equals(level)) {
                        queryCounty();
                    }
                }
            });
        }

    }

    private void showProgressDialig() {
        if (dialog == null) {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("加载中...");
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();
    }

    private void closeProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backView:
                if (current_level == CITY) {
                    queryProvince();
                } else if (current_level == COUNTY) {
                    queryCity();
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (current_level == PROVINCE) {
            selectProvince = provinceList.get(position);
            queryCity();
        } else if (current_level == CITY) {
            selectCity = cityList.get(position);
            queryCounty();
        }
    }
}