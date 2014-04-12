package com.newer.sina.weibo.fragment;

import com.newer.sina.weibo.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {

	private TextView textAppName;
	private TextView textAppAuthor;
	private TextView textAppVersion;
	private TextView textViewWeibo;
	private TextView textViewOAuth;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_about, container, false);
		textAppName = (TextView) view.findViewById(R.id.textView_app_name);
		textAppAuthor = (TextView) view.findViewById(R.id.textView_app_author);
		textAppVersion = (TextView) view.findViewById(R.id.textView_app_version);
		textViewWeibo = (TextView) view.findViewById(R.id.textView_about_weibo);
		textViewOAuth = (TextView) view.findViewById(R.id.textView_about_oauth);
		
		initView();
		
		return view;
	}

	private void initView() {
		textAppName.setText("应用名称： wei博江湖");
		textAppAuthor.setText("开  发  者： sdk");
		textAppVersion.setText("当前版本： 1.0.1");
		textViewWeibo.setText("微博开放平台（Weibo Open Platform）是基于微博海量用户和强大的传播能力，" +
				"接入第三方合作伙伴服务，向用户提供丰富应用和完善服务的开放平台。将你的服务接入微博平台，有助于推广产品，" +
				"增加网站/应用的流量、拓展新用户，获得收益。");
		textViewOAuth.setText("OAuth2.0是OAuth协议的下一版本，但不向后兼容OAuth 1.0。 OAuth 2.0关注客户端" +
				"开发者的简易性，同时为Web应用，桌面应用和手机，和起居室设备提供专门的认证流程。\n在认证和授权的过程中涉及的三方包括：\n" +
				"1、服务提供方，用户使用服务提供方来存储受保护的资源，如照片，视频，联系人列表。\n2、用户，存放在服务提供方的受保护的资源的拥有者。\n" +
				"3、客户端，要访问服务提供方资源的第三方应用。");
	}
}
