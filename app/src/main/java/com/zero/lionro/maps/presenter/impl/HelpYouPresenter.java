package com.zero.lionro.maps.presenter.impl;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.bumptech.glide.Glide;
import com.zero.lionro.maps.R;
import com.zero.lionro.maps.app.MyLeanCloudApp;
import com.zero.lionro.maps.model.impl.MapModel;
import com.zero.lionro.maps.presenter.HelpYouCallBack;
import com.zero.lionro.maps.ui.activity.HelpYouActivity;
import com.zero.lionro.maps.ui.activity.NavigationActivity;
import com.zero.lionro.maps.ui.view.IHelpYouView;
import com.zero.lionro.maps.utils.ToastUtils;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by king on 2017/8/19.
 */

public class HelpYouPresenter {
    private IHelpYouView view;
    private final MapModel mapModel;
    private static List<AVObject> mLists;
    private HelpYouActivity activity;
    private final AVUser currentUser;


    private static final String TAG = "HelpYouPresenter";


    public HelpYouPresenter(IHelpYouView view, HelpYouActivity activity) {
        this.view=view;
        this.activity=activity;
        mapModel = new MapModel();
        currentUser = AVUser.getCurrentUser();
    }

    public void HelpInfo(){
        mapModel.helpYou(view.getRecyclerView(),new HelpYouCallBack() {

             class MyViewHolder extends RecyclerView.ViewHolder {
                 View view;
                 private final TextView tv_item_info;
                 private final TextView tv_item_location;
                 private final TextView tv_item_user;
                 private final TextView help_you_time;
                 private final CircleImageView img_item_headPhotos;

                 public MyViewHolder(View itemView) {
                     super(itemView);
                     view=itemView;
                     img_item_headPhotos = (CircleImageView)itemView.findViewById(R.id.img_item_headPhotos);
                     tv_item_info = (TextView)itemView.findViewById(R.id.tv_item_info);
                     tv_item_location = (TextView)itemView.findViewById(R.id.tv_item_location);
                     tv_item_user = (TextView)itemView.findViewById(R.id.tv_item_user);
                     help_you_time = (TextView)itemView.findViewById(R.id.help_you_time);

                 }
             }

            class MyHelpAdapter extends RecyclerView.Adapter<MyViewHolder>{

                public MyHelpAdapter(List<AVObject> lists) {
                    mLists=lists;
                }

                @Override
                public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_helpinfo, parent, false);
                    final MyViewHolder holder = new MyViewHolder(view);

                    return holder;
                }

                @Override
                public void onBindViewHolder(final MyViewHolder holder, final int position) {
                    final int newPosition = mLists.size()-position-1;
                    holder.tv_item_info.setText((CharSequence) mLists.get(newPosition).get("HelpInfo"));
                    holder.tv_item_location.setText((CharSequence) mLists.get(newPosition).get("Location"));
                    holder.tv_item_user.setText((CharSequence) mLists.get(newPosition).get("UserName"));

                    String headPhoto = mLists.get(newPosition).getAVFile("Head").getUrl();

                    Glide.with(activity).load(headPhoto).crossFade().into(holder.img_item_headPhotos);


                    SimpleDateFormat df = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    }
                    Date date = mLists.get(newPosition).getCreatedAt();
                    String stringDate = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        stringDate = df.format(date);
                    }
                    holder.help_you_time.setText(stringDate);
                    //条目点击事件
                    holder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //得到纬度
                            String stringLatitude = mLists.get(newPosition).get("Latitude").toString();
                            Double latitude = Double.valueOf(stringLatitude);
                            //得到经度
                            String stringLongitude = mLists.get(newPosition).get("Longitude").toString();
                            Double longitude = Double.valueOf(stringLongitude);

                            Intent intent = new Intent(activity, NavigationActivity.class);
                            intent.putExtra("needLatitude",latitude);
                            intent.putExtra("needLongitude",longitude);
                            activity.startActivity(intent);

                        }
                    });

                }
                @Override
                public int getItemCount() {
                    return mLists.size();
                }
            }

            @Override
            public void success(List<AVObject> lists) {
                LinearLayoutManager manager = new LinearLayoutManager(MyLeanCloudApp.getContext());
                RecyclerView recyclerView = view.getRecyclerView();
                recyclerView.setLayoutManager(manager);
                MyHelpAdapter myAdapter = new MyHelpAdapter(lists);
                recyclerView.setAdapter(myAdapter);

            }

            @Override
            public void failed() {
                ToastUtils.showToast("获取数据失败");
            }
        });
    }

}
