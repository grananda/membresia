package controllers.user;

import play.api.libs.mailer.MailerClient;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;

import models.user.AdminUser;
import services.user.AdminUserService;
import views.formData.AdminUserFormData;
import lib.Pager;

import javax.inject.Inject;
import java.util.List;

/**
 * Controller class for AdminUser entity
 */
@With(SecuredAction.class)
public class AdminUserController extends Controller {

    @Inject
    private MailerClient mailer;

    @Inject
    private AdminUserService adminUserService;

    private AdminUserFormData adminUserData;

    private Form<AdminUserFormData> formData;

    private Pager pager;

    private Integer currentPage = 1;


    /**
     * Shows all admin users
     *
     * @return Result
     */
    public Result index(Integer currentPage) {
        this.currentPage = currentPage;
        pager = new Pager(this.currentPage);
        List<AdminUser> adminUsers = adminUserService.getAdminUserList(pager);

        return ok(views.html.user.adminUser.index.render(Messages.get("adminUser.list.global.title"), adminUsers, pager));
    }

    /**
     * Renders admin user's form in creation mode
     *
     * @return Result
     */
    public Result create() {
        adminUserData = new AdminUserFormData();
        formData = adminUserService.setFormData(adminUserData);
        adminUserData.setMode(0);

        return ok(views.html.user.adminUser.form.render(Messages.get("adminUser.form.global.new.title"), formData));
    }

    /**
     * Renders admin user's form in editing mode by user token
     *
     * @param token Unique admin user token identifier
     * @return Result
     */
    public Result edit(String token) {
        adminUserData = new AdminUserFormData();
        adminUserData = adminUserService.setAdminUserData(token);
        adminUserData.setMode(1);
        formData = adminUserService.setFormData(adminUserData);

        return ok(views.html.user.adminUser.form.render(Messages.get("adminUser.form.global.new.title"), formData));
    }

    /**
     * Saves admin user's form data after create or edit
     *
     * @return Result
     */
    public Result save() {
        adminUserData = new AdminUserFormData();
        formData = Form.form(AdminUserFormData.class).bindFromRequest();
        if (formData.hasErrors()) {
            flash("error", Messages.get("app.global.validation.message"));
            return badRequest(views.html.user.adminUser.form.render(Messages.get("adminUser.form.global.new.title"), formData));
        }

        AdminUser user = adminUserService.save(formData);
        if (formData.get().getMode() == 0) adminUserService.sendNewAccountMail(mailer, user);
        flash("success", Messages.get("adminUser.form.save.message.notification", user));
        pager = new Pager(this.currentPage);
        List<AdminUser> users = adminUserService.getAdminUserList(pager);

        return ok(views.html.user.adminUser.index.render(Messages.get("adminUser.list.global.title"), users, pager));
    }

    /**
     * Removes a specific admin user by token
     *
     * @param token Unique user token identifier
     * @return Result
     */
    public Result remove(String token) {
        if (adminUserService.remove(token)) {
            flash("success", Messages.get("member.form.remove.message.success"));
        } else {
            flash("error", Messages.get("member.form.remove.message.error"));
        }
        pager = new Pager(1);
        List<AdminUser> members = adminUserService.getAdminUserList(pager);

        return ok(views.html.user.adminUser.index.render(Messages.get("adminUser.list.global.title"), members, pager));
    }
}
