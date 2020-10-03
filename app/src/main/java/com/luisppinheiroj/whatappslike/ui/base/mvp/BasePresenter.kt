package com.luisppinheiroj.whatappslike.ui.base.mvp

abstract class BasePresenter<V: BaseMvp.View>() :
    BaseMvp.Presenter<V>{

//    var dataManager: DataManager = AppDataManager()
//    val compositeDisposable: CompositeDisposable = CompositeDisposable()
//    val schedulerProvider : SchedulerProvider = SchedulerProvider()

    private var mvpView: V? = null
    private var isViewAttached: Boolean = mvpView != null

    override fun onAttach(mvpView: V?) {
        this.mvpView = mvpView
    }

    override fun getMvpView(): V? = mvpView

    override fun onDetach() {
        //compositeDisposable.dispose()
        mvpView = null
    }





}