import jenkins.model.*
import jenkins.security.*
import org.jenkinsci.plugins.*
import net.sf.json.*;
import org.codefirst.SimpleThemeDecorator;
import org.kohsuke.stapler.*;
import hudson.security.*
import com.cloudbees.plugins.credentials.CredentialsProvider.*

class Actions {
  Actions(out, bindings) {
    this.out = out
    this.bindings = bindings
  }
  def out
  def bindings

  void set_ldap(String ldap_url) {
    def j = Jenkins.getInstance()
    if (ldap_url == 'disabled') {
      j.disableSecurity()
      out.println("LDAP deaktiviert")
      return null
    }
  }

  void set_ldap(String ldap_url, String basedn, String strategy_type, String[] permission_list) {
    def j = Jenkins.getInstance()
    if (ldap_url == 'disabled') {
      j.disableSecurity()
      out.println("LDAP deaktiviert")
      return null
    }
    def realm
    def strategy
    switch (security_model) {
      case 'full_control':
        strategy = new hudson.security.FullControlOnceLoggedInAuthorizationStrategy()
        realm = new hudson.security.HudsonPrivateSecurityRealm(false, false, null)
        break
      case 'unsecured':
        strategy = new hudson.security.AuthorizationStrategy.Unsecured()
        realm = new hudson.security.HudsonPrivateSecurityRealm(false, false, null)
        break
      case 'matrix':
        strategy = new hudson.security.ProjectMatrixAuthorizationStrategy()
        realm = new hudson.security.LDAPSecurityRealm(ldap_url,basedn,'','uid={0}','','','Qd2eysqDO4STwAyPCQHRVA==',false)
        permission_list.each {
          def (perm,role) = it.tokenize( ':' )
          strategy.add(perm,role)
        }
      default:
        throw new InvalidAuthenticationStrategy()
    }

/*
    strategy.add(com.cloudbees.plugins.credentials.CredentialsProvider.VIEW,'ROLE_JENKINSADMIN')
    strategy.add(Computer.BUILD,'ROLE_JENKINSADMIN')
    strategy.add(Computer.CONNECT,'ROLE_JENKINSADMIN')
    strategy.add(Computer.CREATE,'ROLE_JENKINSADMIN')
    strategy.add(Computer.DELETE,'ROLE_JENKINSADMIN')
    strategy.add(Computer.DISCONNECT,'ROLE_JENKINSADMIN')

    strategy.add(Jenkins.ADMINISTER,'ROLE_JENKINSADMIN')
    strategy.add(PluginManager.CONFIGURE_UPDATECENTER,'ROLE_JENKINSADMIN')
    strategy.add(Jenkins.READ,'ROLE_JENKINSADMIN')
    strategy.add(Jenkins.READ,'ROLE_JENKINSUSER')
    strategy.add(Jenkins.READ,'anonymous')
    strategy.add(Jenkins.RUN_SCRIPTS,'ROLE_JENKINSADMIN')
    strategy.add(PluginManager.UPLOAD_PLUGINS,'ROLE_JENKINSADMIN')
    strategy.add(Item.BUILD,'ROLE_JENKINSADMIN')
    strategy.add(Item.CANCEL,'ROLE_JENKINSADMIN')
    strategy.add(Item.CONFIGURE,'ROLE_JENKINSADMIN')
    strategy.add(Item.CREATE,'ROLE_JENKINSADMIN')
    strategy.add(Item.DELETE,'ROLE_JENKINSADMIN')
    strategy.add(Item.DISCOVER,'ROLE_JENKINSADMIN')
    strategy.add(Item.DISCOVER,'ROLE_JENKINSUSER')
    strategy.add(Item.EXTENDED_READ,'ROLE_JENKINSADMIN')
    strategy.add(Item.EXTENDED_READ,'ROLE_JENKINSUSER')
    strategy.add(Item.READ,'ROLE_JENKINSADMIN')
    strategy.add(Item.READ,'ROLE_JENKINSUSER')
    strategy.add(Item.READ,'anonymous')
    strategy.add(Item.WORKSPACE,'ROLE_JENKINSADMIN')
    strategy.add(Item.WORKSPACE,'ROLE_JENKINSUSER')
    strategy.add(Item.WORKSPACE,'anonymous')
    strategy.add(Run.DELETE,'ROLE_JENKINSADMIN')
    strategy.add(Run.UPDATE,'ROLE_JENKINSADMIN')
    strategy.add(Run.UPDATE,'ROLE_JENKINSUSER')
    strategy.add(View.CONFIGURE,'ROLE_JENKINSADMIN')
    strategy.add(View.CREATE,'ROLE_JENKINSADMIN')
    strategy.add(View.DELETE,'ROLE_JENKINSADMIN')
    strategy.add(View.READ,'ROLE_JENKINSADMIN')
    strategy.add(View.READ,'ROLE_JENKINSUSER')
    strategy.add(View.READ,'anonymous')
*/
    j.setSecurityRealm(realm)
    j.setAuthorizationStrategy(strategy)
    j.save()
  }

  void set_swi_style(String cssUrl, String jsUrl) {
    def jsonconfig = new net.sf.json.JsonConfig()
    def json = net.sf.json.JSONSerializer.toJSON((Object)'{"cssUrl":"'+cssUrl+'","jsUrl":"'+jsUrl+'"}',jsonconfig)
    def j = Jenkins.getInstance()
    def p = j.getDescriptor(SimpleThemeDecorator)
    def s = org.kohsuke.stapler.Stapler.getCurrent()
    p.configure(s.getCurrentRequest(),json)
  }
}

def bindings = getBinding()
actions = new Actions(out, bindings)
action = args[0]
if (args.length < 2) {
  actions."$action"()
} else {
  actions."$action"(*args[1..-1])
}
