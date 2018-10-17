package com.merpyzf.xmshare.ui.adapter;

import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.merpyzf.transfermanager.entity.Peer;
import com.merpyzf.xmshare.R;
import com.merpyzf.xmshare.common.Const;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by wangke on 2018/1/12.
 */

public class PeerAdapter extends BaseQuickAdapter<Peer, BaseViewHolder>{

    public PeerAdapter(int layoutResId, @Nullable List<Peer> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Peer item) {

        helper.setText(R.id.tv_nickname, item.getNickName());
        helper.setText(R.id.tv_hostaddress, item.getHostAddress());

        CircleImageView mCivPeerHeader =  helper.getView(R.id.civ_peer_header);
        Glide.with(mContext)
                .load(Const.AVATAR_LIST.get(item.getAvatarPosition()))
                .crossFade()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mCivPeerHeader);


    }
}
