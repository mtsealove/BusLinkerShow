package inha.ac.kr.pdychoo.buslinker.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import inha.ac.kr.pdychoo.buslinker.R;

public class TitleView extends RelativeLayout {
    Context context;
    Button openDrawerBtn;

    public TitleView(Context context) {
        super(context);
        this.context=context;
        init();
    }

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    public TitleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context=context;
        init();
    }

    private void init() {
        String inflaterService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(inflaterService);
        View view = inflater.inflate(R.layout.title_layout, TitleView.this, false);
        openDrawerBtn=view.findViewById(R.id.panelBtn);
        addView(view);
    }
}
