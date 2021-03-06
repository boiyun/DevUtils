package afkt.project.framework.mvp;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.tt.whorlviewlibrary.WhorlView;

import afkt.project.R;
import afkt.project.base.app.BaseMVPToolbarActivity;
import afkt.project.model.bean.ArticleBean;
import afkt.project.ui.adapter.ArticleAdapter;
import butterknife.BindView;
import dev.other.retrofit.RxJavaManager;
import dev.utils.app.ViewUtils;
import dev.utils.common.CollectionUtils;
import dev.widget.assist.ViewAssist;
import dev.widget.function.StateLayout;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * detail: 文章 MVP Activity
 * @author Ttt
 */
public class ArticleMVPActivity extends BaseMVPToolbarActivity<ArticleMVP.Presenter> implements ArticleMVP.View {

    @BindView(R.id.vid_bvr_recy)
    RecyclerView vid_bvr_recy;
    // 加载动画
    WhorlView      vid_sli_load_view;
    // 适配器
    ArticleAdapter articleAdapter;

    @Override
    protected ArticleMVP.Presenter presenter() {
        return new ArticleMVP.Presenter();
    }

    @Override
    public int getLayoutId() {
        return R.layout.base_view_recyclerview;
    }

    @Override
    public void initViews() {
        super.initViews();
        // 初始化 View
        View view = stateLayout.getView(ViewAssist.TYPE_ING);
        vid_sli_load_view = ViewUtils.findViewById(view, R.id.vid_sli_load_view);
    }

    @Override
    public void initValues() {
        super.initValues();
        // 初始化布局管理器、适配器
        articleAdapter = new ArticleAdapter();
        vid_bvr_recy.setAdapter(articleAdapter);
    }

    @Override
    public void initListeners() {
        super.initListeners();
        // 设置监听
        stateLayout.setListener(new StateLayout.Listener() {
            @Override
            public void onRemove(StateLayout layout, int type, boolean removeView) {
            }

            @Override
            public void onNotFound(StateLayout layout, int type) {
                // 切换 View 操作
                if (type == ViewAssist.TYPE_SUCCESS) {
                    ViewUtils.reverseVisibilitys(true, vid_ba_content_linear, vid_ba_state_linear);
                }
            }

            @Override
            public void onChange(StateLayout layout, int type, int oldType, View view) {
                // 判断是否操作成功
                boolean success = (type == ViewAssist.TYPE_SUCCESS);
                // 切换 View 操作
                if (ViewUtils.reverseVisibilitys(success, vid_ba_content_linear, vid_ba_state_linear)) {
                    // 属于请求成功
                } else {
                    if (type == ViewAssist.TYPE_ING) {
                        if (!vid_sli_load_view.isCircling()) {
                            vid_sli_load_view.start();
                        }
                    } else {
                        vid_sli_load_view.stop();
                    }
                }
            }
        });
    }

    @Override
    public void initOtherOperate() {
        super.initOtherOperate();
        // 表示请求中
        stateLayout.showIng();
        // 获取文章列表
        mPresenter.getArticleLists();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止动画
        vid_sli_load_view.stop();
    }

    // ===================
    // = ArticleMVP.View =
    // ===================

    @Override
    public void onArticleListResponse(boolean succeed, ArticleBean articleBean) {
        if (succeed) {
            if (CollectionUtils.isEmpty(articleBean.data.datas)) { // 无数据
                stateLayout.showEmptyData();
            } else { // 请求成功
                stateLayout.showSuccess();
                // 设置数据源
                articleAdapter.setNewInstance(articleBean.data.datas);
            }
        } else { // 请求失败
            stateLayout.showFailed();
        }
    }

    @Override
    public void addDisposable(Disposable disposable) {
        RxJavaManager.getInstance().add(mTag, disposable);
    }
}
