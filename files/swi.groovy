import jenkins.model.*
import jenkins.security.*
import org.jenkinsci.plugins.*
import net.sf.json.*;
import org.codefirst.SimpleThemeDecorator;
import org.kohsuke.stapler.*;
import hudson.security.*
import com.cloudbees.plugins.credentials.CredentialsProvider.*

class InvalidAuthenticationStrategy extends Exception{}

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
    switch (strategy_type) {
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
          int idx = perm.lastIndexOf('.');
          if(idx<0) return null;
          Class cl = Class.forName(perm.substring(0,idx),true, Jenkins.getInstance().getPluginManager().uberClassLoader)
          strategy.add(cl.getAt(perm.substring(idx+1)),role)
        }
        break
      default:
        throw new InvalidAuthenticationStrategy()
    }

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
