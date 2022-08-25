package com.nikola.user.ui.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nikola.user.R;
import com.nikola.user.Utils.Commonutils;
import com.nikola.user.Utils.Const;
import com.nikola.user.Utils.PreferenceHelper;
import com.nikola.user.network.ApiManager.AsyncTaskCompleteListener;
import com.nikola.user.network.ApiManager.VollyRequester;
import com.nikola.user.network.Models.Later;
import com.nikola.user.ui.Fragment.ScheduleDetailBottomSheet;
import com.nikola.user.ui.activity.LaterRequestsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 2/3/2017.
 */

public class LaterAdapter extends RecyclerView.Adapter<LaterAdapter.typesViewHolder> implements AsyncTaskCompleteListener {

    private LaterRequestsActivity mContext;
    private List<Later> itemshistroyList;
    SimpleDateFormat simpleDateFormat;
    SimpleDateFormat inputformat;
    CancelLaterRequest cancelLaterRequest;
    TripsInterface tripsInterface;

    public LaterAdapter(LaterRequestsActivity context, List<Later> itemshistroyList, CancelLaterRequest cancelLaterRequest, TripsInterface tripsInterface) {
        mContext = context;
        simpleDateFormat = new SimpleDateFormat("E, MMM, dd, yyyy hh:mm a");
        inputformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.cancelLaterRequest = cancelLaterRequest;
        this.itemshistroyList = itemshistroyList;
        this.tripsInterface = tripsInterface;
    }

    @Override
    public LaterAdapter.typesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.later_item, null);
        LaterAdapter.typesViewHolder holder = new LaterAdapter.typesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final LaterAdapter.typesViewHolder holder, int position) {
        final Later later_itme = itemshistroyList.get(position);
        if (later_itme != null) {
            String later_Date = "";
            try {
                later_Date = later_itme.getReq_date();
                Date date = inputformat.parse(later_Date);
                later_Date = simpleDateFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.tv_later_service_type.setText(later_itme.getReq_type());
            holder.tv_later_date.setText(later_Date);
            Glide.with(mContext).load(later_itme.getReq_pic()).into(holder.later_car);
            holder.tv_later_source.setText(later_itme.getS_address());
            if(!later_itme.getD_address().equals("")){
                holder.tv_later_destination.setText(later_itme.getD_address());
            } else {
                holder.tv_later_destination.setText(mContext.getResources().getString(R.string.not_available));
            }
            holder.cancel_later.setOnClickListener(view -> cancelLaterRequest.cancelRequest(later_itme.getReq_id()));
            holder.rootLayout.setOnClickListener(view -> {
                ScheduleDetailBottomSheet scheduleDetailBottomSheet = new ScheduleDetailBottomSheet();
                scheduleDetailBottomSheet.setRequestId(later_itme.getReq_id());
                scheduleDetailBottomSheet.show(mContext.getSupportFragmentManager(), scheduleDetailBottomSheet.getTag());
            });
        }
    }

    private void cancelLater(String req_id) {
        Commonutils.progressdialog_show(mContext, "Canceling...");
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ServiceType.CANCEL_LATER_RIDE + Const.Params.ID + "="
                + new PreferenceHelper(mContext).getUserId() + "&" + Const.Params.TOKEN + "="
                +  new PreferenceHelper(mContext).getSessionToken()+"&"+Const.Params.REQUEST_ID + "="+req_id);
        Log.d("mahi", "cancel_reg" + map.toString());
        new VollyRequester(mContext, Const.GET, map, Const.ServiceCode.CANCEL_LATER_RIDE, this);
    }

    @Override
    public int getItemCount() {
        return itemshistroyList.size();
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {

            case Const.ServiceCode.CANCEL_LATER_RIDE:
                Log.e("mahi", "cancel later" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equals("true")) {
                        Commonutils.progressdialog_hide();
                        Commonutils.showtoast(mContext.getResources().getString(R.string.txt_cancel_schedule), mContext);
                    } else {
                        Commonutils.progressdialog_hide();
                        String error = jsonObject.getString("error");
                        Commonutils.showtoast(error, mContext);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    public class typesViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_later_service_type, tv_later_date,tv_later_source,tv_later_destination;
        private ImageButton cancel_later;
        private CircleImageView later_car;
        private CardView rootLayout;

        public typesViewHolder(View itemView) {
            super(itemView);
            later_car = itemView.findViewById(R.id.later_car);
            tv_later_service_type = itemView.findViewById(R.id.tv_later_service_type);
            tv_later_date = itemView.findViewById(R.id.tv_later_date);
            cancel_later = itemView.findViewById(R.id.cancel_later);
            tv_later_source = itemView.findViewById(R.id.tv_later_source);
            tv_later_destination = itemView.findViewById(R.id.tv_later_destination);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }

    public interface CancelLaterRequest
    {
        public void cancelRequest(String requestId);
    }

    public void showLoading() {
        if (tripsInterface != null)
            tripsInterface.onLoadMoreTrips(itemshistroyList.size());
    }

    public interface TripsInterface {
        void onLoadMoreTrips(int skip);
    }
}


