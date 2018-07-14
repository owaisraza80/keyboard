package net.dawateislami.keyboard;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.os.SystemClock;
import android.text.InputType;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NaumanHaider
 */
public class CustomKeyboardView extends KeyboardView
{

	private List<EditText> editTexts = new ArrayList<>();

	private Context context;

	private Keyboard selectedKeyboard;
	private EditText selectedEditText;

	private Activity activity;

	private android.inputmethodservice.Keyboard mKeyboard;

	public enum Keyboard {
		ARABIC,URDU,DEFAULT
	}
	public CustomKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public void showWithAnimation() {
		Animation animation = AnimationUtils
				.loadAnimation(context,
						net.dawateislami.keyboard.R.anim.slide_from_bottom);
		setAnimation(animation);

		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				setVisibility(VISIBLE);
			}
		});
	}

	public void hideWithAnimation() {
		Animation animation = AnimationUtils
				.loadAnimation(context,
						net.dawateislami.keyboard.R.anim.slide_to_bottom);
		setAnimation(animation);
		setAnimation(animation);

		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				setVisibility(GONE);
			}
		});
	}

	public boolean isVisible() {
		return getVisibility() == VISIBLE ;
	}

	public void hideSoftKeyboard(Context context, EditText editText) {
		InputMethodManager inputMethodManager =
				(InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public void selectKeyboard(EditText editText) {
		// Do not show the preview balloons
		this.setPreviewEnabled(true);
		editText.setTextDirection(TEXT_DIRECTION_RTL);
		editText.setGravity(Gravity.RIGHT);

		if (selectedKeyboard == Keyboard.ARABIC) {
			this.hideSoftKeyboard(context,editText);
            mKeyboard = new android.inputmethodservice.Keyboard(context, net.dawateislami.keyboard.R.xml.arabic);
			showWithAnimation();
			this.setVisibility(View.VISIBLE);
			this.setKeyboard(mKeyboard);
			this.setOnKeyboardActionListener(new BasicOnKeyboardActionListener(activity,editText,this));

//            } else if (switchLang.getSelectedItemPosition() == 2) {
//                if (Util.isLangSupported(MainActivity.this, "ગુજરાતી")) {
//                    mKeyboard = new Keyboard(MainActivity.this, R.xml.kbd_guj1);
//                    showKeyboardWithAnimation();
//                    mKeyboardView.setVisibility(View.VISIBLE);
//                    mKeyboardView.setKeyboard(mKeyboard);
//                } else {
//                    Util.displayAlert(MainActivity.this, getResources().getString(R.string.app_name), "Gujarati keyboard is not supported "
//                            + "by your device");
//                    //Reset language selection
//                    switchLang.setSelection(0);
//                    mKeyboard = new Keyboard(MainActivity.this, R.xml
//                            .kbd_hin1);
//                    mKeyboardView.setVisibility(View.GONE);
//
//                    //Show Default Keyboard
//                    InputMethodManager imm =
//                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.showSoftInput(messageEditText, 0);
//                    messageEditText.setOnTouchListener(null);
//                }
		} else if (selectedKeyboard == Keyboard.URDU) {
		    this.hideSoftKeyboard(context,editText);
			mKeyboard = new android.inputmethodservice.Keyboard(context, net.dawateislami.keyboard.R.xml.urdu);
			showWithAnimation();
			this.setVisibility(View.VISIBLE);
			this.setKeyboard(mKeyboard);
			this.setOnKeyboardActionListener(new BasicOnKeyboardActionListener(activity,editText,this));


		} else {
			setVisibility(View.GONE);

			editText.setTextDirection(TEXT_DIRECTION_LTR);
            editText.setGravity(Gravity.LEFT);

			//Show Default Keyboard
			InputMethodManager imm =
					(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(editText, 0);
		}
	}

    public void addEditTextFocusOnKeyboard(final EditText editText) {
        editTexts.add(editText);

        editText.setGravity(Gravity.END);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				if(!editText.isFocusable()) {
					editText.setFocusableInTouchMode(true);
					editText.setFocusable(true);
					editText.requestFocus();
					editText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
					editText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));
				}
				if ((null != selectedEditText && selectedEditText != v) || getVisibility() == GONE) {
					selectedEditText = (EditText) v;
					selectKeyboard(selectedEditText);
				} else {
					hideSoftKeyboard(context,editText);
				}
            }
        });
        // Attach custom keyboard to onFocusChange
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    selectKeyboard((EditText)view);
                } else {
                    hideKeyboardWithAnimation();
                }
            }
        });
        // Fix for cursor movement (based on http://forum.xda-developers.com/showthread.php?t=2497237)
//        editText.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//                if (!isVisible()) {
//                    view.requestFocus();
//                    selectKeyboard((EditText)view);
//                }
//
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                    case MotionEvent.ACTION_MOVE:
//                        EditText editText = (EditText) view;
//                        Layout layout = ((EditText) view).getLayout();
//                        if (layout != null) {
//                            float x = event.getX() + editText.getScrollX();
//                            int offset = layout.getOffsetForHorizontal(0, x);
//                            if (offset > 0) {
//                                if (x > layout.getLineMax(0))
//                                    editText.setSelection(offset);
//                                else
//                                    editText.setSelection(offset - 1);
//                            }
//                        }
//                        break;
//
//                }
//
//                /*
//                int inType = editText.getInputType();       // Backup the input type
//                editText.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
//                editText.onTouchEvent(event);               // Call native handler
//                editText.setInputType(inType);              // Restore input type
//*/
//
//                return true;
//            }
//        });

        // Disable suggestions
        editText.setInputType(editText.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

	public void setKeyboard(Keyboard type) {
		selectedKeyboard = type;
		if(null != selectedEditText)
			selectKeyboard(selectedEditText);
	}

	public void hideKeyboardWithAnimation() {
		if (this.getVisibility() == View.VISIBLE) {
			setVisibility(GONE);
		}
	}

	public class BasicOnKeyboardActionListener implements KeyboardView.OnKeyboardActionListener {

		EditText editText;
		CustomKeyboardView displayKeyboardView;
		private Activity mTargetActivity;

		public BasicOnKeyboardActionListener(Activity targetActivity, EditText editText,
											 CustomKeyboardView
													 displayKeyboardView) {
			mTargetActivity = targetActivity;
			this.editText = editText;
			this.displayKeyboardView = displayKeyboardView;
		}

		@Override
		public void swipeUp() {
			// TODO Auto-generated method stub

		}

		@Override
		public void swipeRight() {
			// TODO Auto-generated method stub

		}

		@Override
		public void swipeLeft() {
			// TODO Auto-generated method stub

		}

		@Override
		public void swipeDown() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onText(CharSequence text) {
			int cursorPosition = editText.getSelectionEnd();
			String before = editText.getText().toString().substring(0, cursorPosition);
			String after = editText.getText().toString().substring(cursorPosition);
			editText.setText(before + text + after);
			editText.setSelection(cursorPosition + 1);
		}

		@Override
		public void onRelease(int primaryCode) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPress(int primaryCode) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
			switch (primaryCode) {
				case 66:
				case 67:
					long eventTime = System.currentTimeMillis();
					KeyEvent event =
							new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0,
									KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);

					mTargetActivity.dispatchKeyEvent(event);
					break;
                case -101:
                    displayKeyboardView
                            .setKeyboard(new android.inputmethodservice.Keyboard(mTargetActivity, net.dawateislami.keyboard.R.xml.symbols_ar));
                    break;
                case -102:
                    displayKeyboardView
                            .setKeyboard(new android.inputmethodservice.Keyboard(mTargetActivity, net.dawateislami.keyboard.R.xml.symbols_shift));
                    break;
                case -103:
                    displayKeyboardView
                            .setKeyboard(mKeyboard);
                    break;
                case -104:
                    displayKeyboardView
                            .setVisibility(GONE);
                    break;
				default:
					break;
			}
		}
	}
}
