package com.cj.fun_orm;

import android.content.Context;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.cj.common.provider.fun$orm.IOrmProvider;

/**
 * Author:chris - jason
 * Date:2019/4/4.
 * Package:com.cj.fun_orm
 * fun_orm对外提供服务的类
 */
@Route(path = "/fun_orm/SEV/com.cj.fun_orm.OrmService")
public class OrmService implements IOrmProvider {


    @Override
    public void init(Context context) {

    }


}
