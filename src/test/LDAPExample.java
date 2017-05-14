package test;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class LDAPExample {

	public static void main(String[] args) throws NamingException {
		Hashtable<String, Object> env = new Hashtable<String, Object>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://localhost:10389/o=jEdu");
		DirContext context = new InitialDirContext(env);
		// => (cn=*Managers)
		DirContext groupCtx = (DirContext) context.lookup("ou=Groups");
		DirContext peapleCtx = (DirContext) context.lookup("ou=People");

		NameParser nameParser = peapleCtx.getNameParser("");

		NamingEnumeration<Binding> groups = groupCtx.listBindings("");
		while (groups.hasMore()) {

			String bindingName = groups.next().getName();
			Attributes groupAttribute = groupCtx.getAttributes(bindingName);
			Attribute description = groupAttribute.get("description");
			Attribute menberAttribute = groupAttribute.get("uniquemember");
			Attribute groupName = groupAttribute.get("cn");

			System.out.println(groupName + " " + description + " nombre of menbers : " + menberAttribute.size());

			NamingEnumeration<?> menbers = menberAttribute.getAll();
			while (menbers.hasMore()) {
				String menberDN = menbers.next().toString();
				Name menberName = nameParser.parse(menberDN);
				DirContext menber = (DirContext) peapleCtx.lookup(menberName.get(2));
				Attributes menberAttributes = menber.getAttributes("",
						new String[] { "cn", "mail", "telephonenumber" });
				System.out.printf("%s, %s , %s \n", menberAttributes.get("cn").get(),
						menberAttributes.get("mail").get(), menberAttributes.get("telephonenumber").get());
			}
		}

		// NamingEnumeration<Binding> enumeration = context.listBindings("");
		// while (enumeration.hasMore()) {
		// System.out.println(enumeration.next().getName());
		// }

	}
}
