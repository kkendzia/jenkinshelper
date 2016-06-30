import jenkins.model.*
import jenkins.security.*
import org.jenkinsci.plugins.*
import net.sf.json.*;
import org.codefirst.SimpleThemeDecorator;
import org.kohsuke.stapler.*;

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
    def j = Jenkins.getInstance()
    realm = new hudson.security.LDAPSecurityRealm('auth.mibstd2.technisat-digital','dc=mibstd2,dc=technisat-digital','','uid={0}','','','Qd2eysqDO4STwAyPCQHRVA==',false)
	j.setSecurityRealm(realm)
    j.save()
    out.println("Setze LDAP"+ldap_url)
  }
  
  void set_swi_style() {
	def jsonconfig = new net.sf.json.JsonConfig()
	def json = net.sf.json.JSONSerializer.toJSON((Object)'{"cssUrl":"/userContent/style.css","jsUrl":"/userContent/style.js"}',jsonconfig)
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
  out.println(args)
}


