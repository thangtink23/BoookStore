package com.bkpirates.fragment;

import java.io.IOException;
import java.text.ParseException;

import org.apache.http.HttpResponse;

import com.bkpirates.bookstore.AdminActivity;
import com.bkpirates.bookstore.MainActivity;
import com.bkpirates.bookstore.R;
import com.bkpirates.entity.AccountEntity;
import com.bkpirates.webservice.NetWork;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

@SuppressLint("NewApi")
public class LoginFragment extends Fragment {

	private final String TAG;

	private Button signIn;
	private Button crtAccount;
	private EditText phoneNumber;
	private EditText passWord;
	private String phone;
	private String pass;
	NetWork netWork = new NetWork();
	private int check = 0;
	public static int checkLogin = 0;
	private final String CHECK_ACCOUNT = "http://camlinhshop.com/JSON_API/check_account.php";

	public static AccountEntity accEntity = new AccountEntity();

	public LoginFragment(String tag) {
		this.TAG = tag;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login, null);
		signIn = (Button) view.findViewById(R.id.btnSignIn);
		crtAccount = (Button) view.findViewById(R.id.btnCreateAccount);
		phoneNumber = (EditText) view.findViewById(R.id.phoneNumber);
		passWord = (EditText) view.findViewById(R.id.passWord);
		// phoneNumber.setHintTextColor(getResources().getColor(R.color.white));
		signIn.setEnabled(false);

		phoneNumber.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (phoneNumber.getText().toString().equals("") == false
						&& passWord.getText().toString().equals("") == false) {
					signIn.setEnabled(true);
					signIn.setBackgroundResource(R.drawable.buttonshape1);
					// signIn.setTextColor(R.string.CodeColor);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		passWord.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (phoneNumber.getText().toString().equals("") == false
						&& passWord.getText().toString().equals("") == false) {
					signIn.setEnabled(true);
					signIn.setBackgroundResource(R.drawable.buttonshape1);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		passWord.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				passWord.setText("");
				return false;
			}
		});
		signIn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doLogin();

			}
		});
		crtAccount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				CrtAccountFragment caf = new CrtAccountFragment();
				ft.replace(R.id.container, caf);
				ft.addToBackStack(null);
				ft.commit();
				fm.executePendingTransactions();
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		SharedPreferences pre = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
		phoneNumber.setText(pre.getString("phone", ""));
		passWord.setText(pre.getString("pass", ""));
		doLogin();
		super.onResume();
	}

	@Override
	public void onPause() {
		SharedPreferences pre = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pre.edit();
		if (!phoneNumber.getText().toString().equals("1") && !passWord.getText().toString().equals("1")) {

			editor.putString("checkLogin", checkLogin + "");
			editor.putString("phone", phoneNumber.getText().toString());
			editor.putString("pass", passWord.getText().toString());
			editor.commit();
		}

		super.onPause();
	}

	public class LoginAsyncTask extends AsyncTask<String, Void, String> {
		ProgressDialog pb;

		@Override
		protected void onPreExecute() {
			pb = new ProgressDialog(getActivity());
			pb.setMessage("Login...");
			pb.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String s) {
			if (pb != null) {
				pb.dismiss();
			}
			if (s != null) {
				accEntity = netWork.checkAccountForLogin(s);
				check = Integer.parseInt(accEntity.getPassword());
				if (check == 2) {

					Intent intent = new Intent(getActivity(), AdminActivity.class);
					startActivity(intent);
					phoneNumber.setText("");
					passWord.setText("");
				} else if (check == 1) {
					// Login success
					accEntity.setPhone(phone);
					accEntity.setPassword(pass);

					checkLogin = 1;
					// login thanh cong thi luu tai khoan vao file
					SharedPreferences pre = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = pre.edit();
					editor.putString("checkLogin", 1 + "");
					editor.putString("phone", phone);
					editor.putString("pass", pass);
					editor.commit();

					hideVirtualKeyboard();

					FragmentManager fm = getActivity().getSupportFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();

					if (TAG == MainActivity.AccountTag) {
						AccountFragment fragment = new AccountFragment();
						ft.replace(R.id.container, fragment);
					} else if (TAG == MainActivity.CartTag) {
						CartFragment fragment = new CartFragment();
						ft.replace(R.id.container, fragment);
					}
					// ft.addToBackStack(null);
					ft.commit();
					fm.executePendingTransactions();
				} else {

					AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
					dialog.setTitle("Lỗi");
					dialog.setMessage("Tài khoản hoặc mật khẩu không đúng!");
					dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});

					dialog.setCancelable(false);
					dialog.create();
					dialog.show();
					SharedPreferences pre = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = pre.edit();
					editor.clear();
					editor.commit();
				}
			}
			super.onPostExecute(s);
		}

		@Override
		protected String doInBackground(String... params) {
			String url = params[0];
			HttpResponse response = null;
			try {
				response = netWork.makeRquestLogin(url);
			} catch (IOException e) {
				return null;
			}
			if (response != null) {
				String content = null;
				try {
					content = netWork.processHTTPResponce(response);
					return content;
				} catch (IOException e) {
					return null;
				} catch (ParseException e) {
					return null;
				}
			}
			return null;

		}
	}
// thuc hien dang nhap neu có két noi mang thì dang nhap khong thì thong bao loi
	private void doLogin() {
		phone = phoneNumber.getText().toString();
		pass = passWord.getText().toString();

		if (phone.length() == 0 || pass.length() == 0)
			return;
		else {
			netWork.setPhone(phone);
			netWork.setPass(pass);
			if (netWork.checkInternetConnect(getActivity())) {
				LoginAsyncTask nw = (LoginAsyncTask) new LoginAsyncTask().execute(CHECK_ACCOUNT+ "?uid=" + phone+ "&pass=" +pass);
			} else {
				AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
				dialog.setTitle("Lỗi").setCancelable(false).setMessage("Vui lòng kiểm tra kết nối mạng!")
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						});
				dialog.create().show();
			}
		}
	}

	private void hideVirtualKeyboard() {
		// hide the virtual key board
		InputMethodManager inputManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(
				(null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

}