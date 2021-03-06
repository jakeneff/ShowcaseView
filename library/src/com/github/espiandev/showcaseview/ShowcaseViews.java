package com.github.espiandev.showcaseview;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.github.espiandev.showcaseview.ShowcaseView.ConfigOptions;
import com.github.espiandev.showcaseview.ShowcaseView.OnSetVisibilityListener;

public class ShowcaseViews {

    private final List<ShowcaseView> views = new ArrayList<ShowcaseView>();
    private final Activity activity;
    private final int showcaseTemplateId;
    private ShowcaseView currentShowcaseView;
    private OnShowcaseAcknowledged showcaseAcknowledgedListener = new OnShowcaseAcknowledged() {
        @Override
        public void onShowCaseAcknowledged(ShowcaseView showcaseView) {
            //DEFAULT LISTENER - DOESN'T DO ANYTHING!
        }
    };

    public interface OnShowcaseAcknowledged {
        void onShowCaseAcknowledged(ShowcaseView showcaseView);
    }

    public ShowcaseViews(Activity activity, int showcaseTemplateLayout) {
        this.activity = activity;
        this.showcaseTemplateId = showcaseTemplateLayout;
    }

    public ShowcaseViews(Activity activity, int showcaseTemplateLayout, OnShowcaseAcknowledged acknowledgedListener) {
        this(activity, showcaseTemplateLayout);
        this.showcaseAcknowledgedListener = acknowledgedListener;
    }

    public void addView(ItemViewProperties properties, ConfigOptions configOptions) {
        ShowcaseViewBuilder builder = new ShowcaseViewBuilder(activity, showcaseTemplateId)
                .setText(properties.titleResId, properties.messageResId)
                .setShowcaseIndicatorScale(properties.scale)
        		.setShowcaseOffset(properties.xOffset, properties.yOffset)
        		.setShowcaseConfigOptions(configOptions);

        if(showcaseActionBar(properties)) {
            builder.setShowcaseItem(properties.itemType, properties.id, activity);
        } else {
            builder.setShowcaseView(activity.findViewById(properties.id));
        }

        ShowcaseView showcaseView = builder.build();
        showcaseView.overrideButtonClick(createShowcaseViewDismissListener(showcaseView));
        showcaseView.setVisibilityListener(showcaseViewOnSetVisibilityListener);
        
        showcaseView.overrideButtonClick(createShowcaseViewDismissListener(showcaseView));
        views.add(showcaseView);
    }

    private boolean showcaseActionBar(ItemViewProperties properties) {
        return properties.itemType > ItemViewProperties.ID_NOT_IN_ACTIONBAR;
    }

    private View.OnClickListener createShowcaseViewDismissListener(final ShowcaseView showcaseView) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcaseView.hide();
                if (views.isEmpty()) {
                    showcaseAcknowledgedListener.onShowCaseAcknowledged(showcaseView);
                }
            }
        };
    }
    
    OnSetVisibilityListener showcaseViewOnSetVisibilityListener = new OnSetVisibilityListener() {
		@Override
		public void onSetVisibility() {
			if (!views.isEmpty()) {
                show();
            }
		}
	};
    
    public void show() {
        if (views.isEmpty()) {
            return;
        }
        final ShowcaseView view = views.get(0);
        ((ViewGroup) activity.getWindow().getDecorView()).addView(view);
        currentShowcaseView = view; 
        views.remove(0);
    }

    public boolean hasViews(){
        return !views.isEmpty();
    }
    
    public void hideAll() {
    	if (null != currentShowcaseView) {
	    	currentShowcaseView.hide();
	    	for (ShowcaseView showcaseView : views) {
	    		showcaseView.hide();
	    	}
	    }
    }

    public static class ItemViewProperties {

        public static final int ID_NOT_IN_ACTIONBAR = -1;
        public static final int ID_SPINNER = 0;
        public static final int ID_TITLE = 1;
        public static final int ID_OVERFLOW = 2;
        private static final float DEFAULT_SCALE = 1f;
        private static final float NO_X_OFFSET = 0;
        private static final float NO_Y_OFFSET = 0;

        protected final int titleResId;
        protected final int messageResId;
        protected final int id;
        protected final int itemType;
        protected final float scale;
        protected final float xOffset;
        protected final float yOffset;

        public ItemViewProperties(int id, int titleResId, int messageResId) {
            this(id, titleResId, messageResId, ID_NOT_IN_ACTIONBAR, DEFAULT_SCALE, NO_X_OFFSET, NO_Y_OFFSET);
        }

        public ItemViewProperties(int id, int titleResId, int messageResId, float scale) {
            this(id, titleResId, messageResId, ID_NOT_IN_ACTIONBAR, scale, NO_X_OFFSET, NO_Y_OFFSET);
        }

        public ItemViewProperties(int id, int titleResId, int messageResId, int itemType) {
            this(id, titleResId, messageResId, itemType, DEFAULT_SCALE, NO_X_OFFSET, NO_Y_OFFSET);
        }
        
        public ItemViewProperties(int id, int titleResId, int messageResId, float xOffset, float yOffset) {
        	this(id, titleResId, messageResId, ID_NOT_IN_ACTIONBAR, DEFAULT_SCALE, xOffset, yOffset);
        }
        
        public ItemViewProperties(int id, int titleResId, int messageResId, float scale, float xOffset, float yOffset) {
        	this(id, titleResId, messageResId, ID_NOT_IN_ACTIONBAR, scale, xOffset, yOffset);
        }

        public ItemViewProperties(int id, int titleResId, int messageResId, int itemType, float scale, float xOffset, float yOffset) {
            this.id = id;
            this.titleResId = titleResId;
            this.messageResId = messageResId;
            this.itemType = itemType;
            this.scale = scale;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }
}
