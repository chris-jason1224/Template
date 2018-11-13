package com.cj.share_login.share;

import android.app.Activity;
import android.content.Context;

import com.cj.share_login.share.entity.ShareEntity;

/**
 * Created by mayikang on 2018/10/11.
 */

public class CJShare {
    private static Activity context;
    private OnShareResult result;

    private CJShare() {

    }

    private static class Holder {
        private static final CJShare instance = new CJShare();
    }

    public static CJShare getInstance(Activity act) {
        context=act;
        return Holder.instance;
    }

    public void doShare(final ShareEntity entity, final OnShareResult result) {

        new PopShareViewUtil(context, new PopShareViewUtil.ShareClick() {
            @Override
            public void onClick(int type) {
                entity.setPlatform(type);
                ShareUtil.getInstance().share(context, entity, new ShareUtil.ShareCallback() {
                    @Override
                    public void onSuccess() {
                        if (result != null) {
                            result.onSuccess();
                        }
                    }

                    @Override
                    public void onCancel() {
                        if (result != null) {
                            result.onCancel();
                        }
                    }

                    @Override
                    public void onError() {
                        if (result != null) {
                            result.onError();
                        }
                    }
                });
            }
        }).show();

    }

    public interface OnShareResult {
        void onSuccess();

        void onCancel();

        void onError();
    }

}
