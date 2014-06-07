package zekai.com;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

public class SettingActivity extends Activity{
	private int limit_time;
	private EditText limit_time_detial; 
	private Button sure;
	private Button cancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.float_setting_activity);
		//屏蔽窗口之外的点击
		this.setFinishOnTouchOutside(false);
		
		Intent intent = getIntent();
		limit_time = intent.getIntExtra("limit_time", 0);
		
		findViews();
		
		sure.setOnClickListener(new SureListener());
		cancel.setOnClickListener(new CancelListener());
	}
	
	public void findViews()
	{
		limit_time_detial = (EditText)findViewById(R.id.limit_time);
		limit_time_detial.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		sure = (Button)findViewById(R.id.setting_sure);
		cancel = (Button)findViewById(R.id.setting_cancel);
		
		limit_time_detial.setText(limit_time + "");
	}
	
	class SureListener implements OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			limit_time = Integer.parseInt(limit_time_detial.getText().toString());
			Intent intent = new Intent();
			intent.putExtra("limit_time", limit_time);
			setResult(1, intent);
			finish();
		}
	}
	
	class CancelListener implements OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.putExtra("limit_time", limit_time);
			setResult(1, intent);
			finish();
		}		
	}
}
