package cn.wizzer.modules.back.robot.controllers;

import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.back.robot.models.Rb_user;
import cn.wizzer.modules.back.robot.services.RbUserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.*;
import org.nutz.dao.Chain;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.WhaleAdaptor;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by wizzer on 2016/7/23.
 */
@IocBean
@At("/private/robot/user")
@Filters({@By(type = PrivateFilter.class)})
public class RbUserController {
    private static final Log log = Logs.get();
    @Inject
    RbUserService rbUserService;

    @At("")
    @Ok("beetl:/private/robot/user/index.html")
    @RequiresAuthentication
    public void index() {
    }


    @At("/edit/?")
    @Ok("beetl:/private/robot/user/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {
        return rbUserService.fetch(id);
    }

    @At
    @Ok("json")
    @RequiresPermissions("order.robot.user.edit")
    public Object editDo(@Param("..") Rb_user user, HttpServletRequest req) {
        try {
            user.setOpBy(Strings.sNull(req.getAttribute("uid")));
            user.setOpAt((int) (System.currentTimeMillis() / 1000));
            rbUserService.updateIgnoreNull(user);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/enable/?")
    @Ok("json")
    @RequiresPermissions("order.robot.user.edit")
    public Object enable(String qq, HttpServletRequest req) {
        try {
            rbUserService.update(Chain.make("disabled", false), Cnd.where("qq", "=", qq));
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/disable/?")
    @Ok("json")
    @RequiresPermissions("order.robot.user.edit")
    public Object disable(String qq, HttpServletRequest req) {
        try {
            rbUserService.update(Chain.make("disabled", true), Cnd.where("qq", "=", qq));
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At
    @Ok("json:full")
    @RequiresAuthentication
    public Object data(@Param("qq") String qq, @Param("name") String name, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
        if (!Strings.isBlank(qq))
            cnd.and("qq", "like", "%" + qq + "%");
        if (!Strings.isBlank(name))
            cnd.and("name", "like", "%" + name + "%");
        return rbUserService.data(length, start, draw, order, columns, cnd, null);
    }
}
