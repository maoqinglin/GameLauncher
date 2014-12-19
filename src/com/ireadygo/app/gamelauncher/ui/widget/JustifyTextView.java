package com.ireadygo.app.gamelauncher.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author ccheng
 * @Date 3/18/14
 */
public class JustifyTextView extends TextView {
	private static final String ENDING_MARK = "\r";
	private static final char ENDING_MARK_CHAR = '\r';

	private int mLineY;
	private int mViewWidth;
	public static final String TWO_CHINESE_BLANK = "  ";

	public JustifyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom + 10);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		TextPaint paint = getPaint();
		paint.setColor(getCurrentTextColor());
		paint.drawableState = getDrawableState();
		mViewWidth = getMeasuredWidth();
		String text = getText().toString();
		mLineY = 0;
		mLineY += getTextSize();
		Layout layout = getLayout();

		// layout.getLayout()在4.4.3出现NullPointerException
		if (layout == null) {
			return;
		}

		Paint.FontMetrics fm = paint.getFontMetrics();

		int textHeight = (int) (Math.ceil(fm.descent - fm.ascent));
		textHeight = (int) (textHeight * layout.getSpacingMultiplier() + layout.getSpacingAdd());

		for (int i = 0; i < layout.getLineCount(); i++) {
			int lineStart = layout.getLineStart(i);
			int lineEnd = layout.getLineEnd(i);
			float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, getPaint());
			String line = text.substring(lineStart, lineEnd);
//			Log.d("liu.js", "needScale--" + needScale(line) + "|line=" + line);
			if (needScale(line)) {
				drawScaledText(canvas, lineStart, line, width);
			} else {
				canvas.drawText(line, 0, mLineY, paint);
			}
			// modify by liu.js 2014/11/14 mLineY += textHeight
			mLineY += textHeight - 1;
		}
	}

	private void drawScaledText(Canvas canvas, int lineStart, String line, float lineWidth) {
		float x = 0;
		if (isFirstLineOfParagraph(lineStart, line)) {
			String blanks = "  ";
			canvas.drawText(blanks, x, mLineY, getPaint());
			float bw = StaticLayout.getDesiredWidth(blanks, getPaint());
			x += bw;

			line = line.substring(3);
		}

		int gapCount = line.length() - 1;
		int i = 0;
		if (line.length() > 2 && line.charAt(0) == 12288 && line.charAt(1) == 12288) {
			String substring = line.substring(0, 2);
			float cw = StaticLayout.getDesiredWidth(substring, getPaint());
			canvas.drawText(substring, x, mLineY, getPaint());
			x += cw;
			i += 2;
		}

		float d = (mViewWidth - lineWidth) / gapCount;
		for (; i < line.length(); i++) {
			String c = String.valueOf(line.charAt(i));
			float cw = StaticLayout.getDesiredWidth(c, getPaint());
			canvas.drawText(c, x, mLineY, getPaint());
			x += cw + d;
		}
	}

	private boolean isFirstLineOfParagraph(int lineStart, String line) {
		return line.length() > 3 && line.charAt(0) == ' ' && line.charAt(1) == ' ';
	}

	private boolean needScale(String line) {
		if (line == null || line.length() == 0) {
			return false;
		} else {
			char c = line.charAt(line.length() - 1);
			return (c != ENDING_MARK_CHAR) && (c != '\n');
		}
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		String textStr = text.toString();
		if (textStr.endsWith("\n")) {
			textStr = textStr.replaceAll("\n", "");
		}
		textStr += ENDING_MARK;
		super.setText(textStr, type);
	}
}
