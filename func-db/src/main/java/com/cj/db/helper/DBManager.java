package com.cj.db.helper;

import android.content.Context;
import com.cj.common.util.AndroidSystemUtil;
import com.cj.db.dao.DaoMaster;
import com.cj.db.dao.DaoSession;

import org.greenrobot.greendao.database.Database;


/**
 * Created by mayikang on 2018/10/17.
 */

/**
 * 负责数据库的初始化和管理工作
 */
public class DBManager {

    /**
     * DaoMaster：daomaster以一定的模式持有数据库对象（SQLiteDatabase）并管理一些DAO类（而不是对象）。
     有一个静态的方法创建和drop数据库表。它的内部类OpenHelper和DevOpenHelper是SQLiteOpenHelper的实现类，
     用于创建SQLite数据库的模式。

     DaoSession：
     管理指定模式下所有可用的DAO对象，你可以通过某个get方法获取到。DaoSession提供一些通用的持久化方法，比如
     对实体进行插入，加载，更新，刷新和删除。最后DaoSession对象会跟踪identity scope。

     DAOs（Data access objects）:
     数据访问对象，用于实体的持久化和查询。对于每一个实体，greenDao会生成一个DAO，相对于DaoSession它拥有更多
     持久化的方法，比如：加载全部，插入（insertInTx，语境不明了，暂且简单的翻译成插入）。
     */


    private DaoSession daoSession;

    private DaoMaster.DevOpenHelper helper;

    private DBManager(){

    }

    private static class Holder{
        private static final DBManager instance = new DBManager();
    }

    public static DBManager getInstance(){
        return Holder.instance;
    }

    public void initDB(Context context){

        helper=new DaoMaster.DevOpenHelper(context,AndroidSystemUtil.getInstance().getAppName(context)+".db");

        Database db=helper.getWritableDb();

        daoSession=new DaoMaster(db).newSession();

    }


    //获取daoSession
    public DaoSession getDaoSession(){
        return daoSession;
    }

    //关闭数据库连接
    public void closeDBConnection(){

        //1.关闭DaoSession
        if(daoSession!=null){
            daoSession.clear();
            daoSession=null;
        }

        //2.关闭helper连接
        if(helper!=null){
            helper.close();
            helper=null;
        }

    }


}
