package services;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import play.api.libs.mailer.MailerClient;
import play.i18n.Messages;
import play.libs.mailer.Email;
import models.Member;
import play.data.Form;
import services.contract.MemberServiceInterface;
import services.formData.MemberFormData;

import java.util.List;

/**
 * Middleware class for controller model interaction and other member related business logic
 */
public class MemberService implements MemberServiceInterface {

    /**
     * {@inheritDoc}
     */
    public MemberFormData setMemberData(String token) {

        return new MemberFormData(getModel().getMemberByToken(token));
    }

    /**
     * {@inheritDoc}
     */
    public Form<MemberFormData> setFormData(MemberFormData memberData) {

        return Form.form(MemberFormData.class).fill(memberData);
    }

    /**
     * {@inheritDoc}
     */
    public List<Member> getMemberList(Pager pager) {

        return getModel().getMemberList(pager);
    }

    /**
     * {@inheritDoc}
     */
    public Member save(Form<MemberFormData> formData) {

        Member member = (formData.get().getId() != null) ? getModel().getMemberById(formData.get().getId()) : getModel();
        member.getSubscriptions().clear();
        member.setData(formData.get());
        member.save();
        return member;
    }

    /**
     * {@inheritDoc}
     */
    public boolean remove(String token) {

        return getModel().remove(token);
    }

    /**
     * {@inheritDoc}
     */
    public Member getMember(String token) {

        return getModel().getMemberByToken(token);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMemberEmailUsed(String email, String token) {

        return getModel().getUserEmailCount(email, token) > 0;
    }

    /**
     * Creates Member model object
     *
     * @return Member
     */
    private Member getModel() {

        return new Member();
    }

    /**
     * {@inheritDoc}
     */
    public void sendNewAccountMail(MailerClient mailer, Member member) {

        Config conf = ConfigFactory.load();
        String sendFromEmail = conf.getString("play.mailer.user");
        String sendFromName = Messages.get("app.global.title");
        String subject = sendFromName + ": " + Messages.get("adminUser.mail.subject.newAccount");

        Email email = new Email();
        email.setSubject(subject);
        email.setFrom(sendFromName + "<" + sendFromEmail + ">");
        email.addTo(member.toString() + "<" + member.getEmail() + ">");
        String body = views.html.member.newMemberMail.render(subject, member).body();
        email.setBodyHtml(body);

        mailer.send(email);
    }
}
