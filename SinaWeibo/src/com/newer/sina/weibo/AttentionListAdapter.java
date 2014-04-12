package com.newer.sina.weibo;

import java.util.List;

import com.newer.sina.weibo.dao.Attention;
import com.squareup.picasso.Picasso;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AttentionListAdapter extends BaseAdapter {

	private static final String TAG = null;
	private Context context;
	private List<Attention> dataSet;
	private LayoutInflater inflater;

	public AttentionListAdapter(Context context, List<Attention> dataSet) {
		this.context = context;
		this.dataSet = dataSet;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return dataSet.size();
	}

	@Override
	public Object getItem(int position) {
		return dataSet.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		ImageView imageView;
		TextView textViewName;
		TextView textViewLocation;
		TextView textViewDescription;
		Button button;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.attention_list_item, null);
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.imageView_attention);
			holder.textViewName = (TextView) convertView
					.findViewById(R.id.textView_attention_name);
			holder.textViewLocation = (TextView) convertView
					.findViewById(R.id.textView_attention_location);
			holder.textViewDescription = (TextView) convertView
					.findViewById(R.id.textView_attention_description);
			holder.button = (Button) convertView
					.findViewById(R.id.button_attention);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Attention attention = dataSet.get(position);
		//加载图片
		Picasso.with(context).load(attention.avatar_large)
				.into(holder.imageView);
		holder.textViewName.setText(attention.screen_name);
		holder.textViewLocation.setText(attention.location);
		holder.textViewDescription.setText(":" + attention.description);
		holder.button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (holder.button.getText().equals("取消关注")) {
					holder.button.setText("关注");
					// 执行取消关注某人的操作

				} else {
					holder.button.setText("取消关注");
					// 执行关注某人的操作

				}
			}
		});

		Log.d(TAG, "关注项：screen_name=" + attention.screen_name + ",location="
				+ attention.location);
		return convertView;
	}

}
