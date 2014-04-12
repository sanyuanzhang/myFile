package com.newer.sina.weibo;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.sina.weibo.sdk.openapi.models.Status;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CollectionAdatper extends BaseAdapter {

	private static final String TAG = "CollectionAdatper";
	private Context context;
	private List<Status> dataSet;
	private LayoutInflater inflater;

	public CollectionAdatper(Context context, List<Status> dataSet) {
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
		ImageView imageViewUser;
		TextView textViewName;
		TextView textViewDate;
		TextView textViewContent;
		ImageView imageViewContent;
		TextView repostsCount;
		TextView commentsCount;
		TextView attitudesCount;
		TextView textViewSource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.collection_list_item, null);
			holder.imageViewUser = (ImageView) convertView
					.findViewById(R.id.imageView_collection);
			holder.textViewName = (TextView) convertView
					.findViewById(R.id.textView_collection_name);
			holder.textViewDate = (TextView) convertView
					.findViewById(R.id.textView_collection_date);
			holder.textViewContent = (TextView) convertView
					.findViewById(R.id.textView_collection_text);
			holder.imageViewContent = (ImageView) convertView
					.findViewById(R.id.imageView_collection_image);
			holder.repostsCount = (TextView) convertView
					.findViewById(R.id.textView_collection_reposts_count);
			holder.commentsCount = (TextView) convertView
					.findViewById(R.id.textView_collection_comments_count);
			holder.attitudesCount = (TextView) convertView
					.findViewById(R.id.textView_collection_attitudes_count);
			holder.textViewSource = (TextView) convertView
					.findViewById(R.id.textView_collection_source);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Status status = dataSet.get(position);
		// 加载图片
		Picasso.with(context).load(status.user.avatar_large)
				.into(holder.imageViewUser);
		holder.textViewName.setText(status.user.screen_name);
		holder.textViewDate.setText("发布：" + new Date(status.created_at).toLocaleString());
		holder.textViewContent.setText(status.text);
		// holder.imageViewContent
		if (!status.thumbnail_pic.equals("")) {
			Picasso.with(context).load(status.thumbnail_pic)
					.into(holder.imageViewContent);
		}
		holder.repostsCount.setText("转发："
				+ String.valueOf(status.reposts_count));
		holder.commentsCount.setText("评论："
				+ String.valueOf(status.comments_count));
		holder.attitudesCount.setText("表态："
				+ String.valueOf(status.attitudes_count));
		holder.textViewSource.setText("来源：" + status.source.substring(
				status.source.indexOf(">") + 1, status.source.length() - 4));

		return convertView;
	}

}
