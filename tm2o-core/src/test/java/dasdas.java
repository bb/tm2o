import java.io.File;
import java.util.Properties;

import org.odata4j.core.OEntity;
import org.odata4j.core.OLink;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.producer.EntitiesResponse;
import org.odata4j.producer.InlineCount;
import org.odata4j.producer.QueryInfo;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapix.io.CTMTopicMapReader;

import de.topicmapslab.odata.TopicMapODataProducer;
import de.topicmapslab.odata.content.memory.MemoryOdataContentProvider;
import de.topicmapslab.odata.content.remote.RemoteTmqlHelper;
import de.topicmapslab.odata.dao.AssociationTypeDAO;
import de.topicmapslab.odata.dao.NameTypeDAO;
import de.topicmapslab.odata.dao.OccurrenceTypeDAO;
import de.topicmapslab.odata.dao.TopicTypeDAO;
import de.topicmapslab.tmql4j.components.processor.runtime.ITMQLRuntime;
import de.topicmapslab.tmql4j.components.processor.runtime.TMQLRuntimeFactory;
import de.topicmapslab.tmql4j.path.components.processor.runtime.TmqlRuntime2007;

public class dasdas {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		tmql();
	}

	public static final void tmql() throws Exception {
		TopicMap tm = TopicMapSystemFactory.newInstance().newTopicMapSystem().createTopicMap("http://e");
		CTMTopicMapReader reader = new CTMTopicMapReader(tm, new File(
				"C:/Programme/Apache Software Foundation/Tomcat 7.0/webapps/odata4j-web/WEB-INF/data/odata.ctm"));
		reader.read();
		String q = "FOR $t IN http://psi.topicmapslab.de/p/odata/person >> instances FOR $ot IN http://psi.topicmapslab.de/p/odata/institution RETURN fn:uniq ( $t << players << roles [ fn:count ( . >> scope ) == 0 ] >> roles >> players $ot MINUS $t )";

		ITMQLRuntime runtime = TMQLRuntimeFactory.newFactory().newRuntime(TmqlRuntime2007.TMQL_2007);
		System.out.println(runtime.run(tm, q).getResults());
	}

	public static final void local() throws Exception {

		Properties properties = new Properties();
		properties.put("path", "C:/Programme/Apache Software Foundation/Tomcat 7.0/webapps/odata4j-web/WEB-INF/data/toytm.ctm");
		properties.put("base-locator", "http://lala.de");

		String topicMapId = "123";
		TopicMapODataProducer producer = new TopicMapODataProducer(MemoryOdataContentProvider.class, properties, "Lala");
		EdmDataServices service = producer.getMetadata(topicMapId);
		for (EdmEntityType type : service.getEntityTypes()) {
			QueryInfo queryInfo = new QueryInfo(InlineCount.NONE, null, null, null, null, null, null, null, null);
			EntitiesResponse res = producer.getEntities(topicMapId, type.name, queryInfo);
			if (!res.getEntities().isEmpty()) {
				for (OEntity entity : res.getEntities()) {
					System.out.println("+++++++++++++++++++++++++");
					System.out.println("Counter players of :");
					print(entity);
					for (OLink link : entity.getLinks()) {
						String navProp = link.getRelation();
						EntitiesResponse r = producer.getNavProperty(topicMapId, type.name, entity.getId(), navProp, queryInfo);
						if (!r.getEntities().isEmpty()) {
							System.out.println("Navigation property: " + navProp);
							System.out.println("=======================");
							for (OEntity o : r.getEntities()) {
								print(o);
								System.out.println("----------------------");
							}
						}
					}
					System.out.println("+++++++++++++++++++++++++");
				}
			}
		}

	}

	public static final void print(OEntity entity) {
		System.out.println("Id : " + entity.getId());
		for (OProperty<?> prop : entity.getProperties()) {
			System.out.println(prop.getName() + ": " + prop.getValue().toString());
		}
	}

	public static final void remote() {

		final String uri = "http://localhost:9090/majortom-server";
		final String topicMapId = "fe717c45763d7b36067cc17a4b1b5d6";
		RemoteTmqlHelper helper = new RemoteTmqlHelper(uri, topicMapId);

		for (TopicTypeDAO dao : helper.getTopicTypes()) {
			System.out.println("DAO: " + dao.getTypeLabel() + "(" + dao.getTypeId() + ")");
			for (NameTypeDAO nDao : helper.getNameTypes(dao.getTypeId())) {
				System.out.print("Name-DAO: " + nDao.getTypeLabel() + "(" + nDao.getTypeId() + ") @ ");
				for (TopicTypeDAO theme : nDao.getThemeDAOs()) {
					System.out.print(theme.getTypeLabel() + " ");
				}
			}
			System.out.println();
			for (OccurrenceTypeDAO oDao : helper.getOccurrenceTypes(dao.getTypeId())) {
				System.out.print("Occ-DAO: " + oDao.getTypeLabel() + "(" + oDao.getTypeId() + ") ^^ " + oDao.getDatatype() + " @ ");
				for (TopicTypeDAO theme : oDao.getThemeDAOs()) {
					System.out.print(theme.getTypeLabel() + " ");
				}
			}
			System.out.println("----------------------------");
			System.out.println();
		}

		for (AssociationTypeDAO dao : helper.getAssociationSignatures()) {
			System.out.print("Association: " + dao.getTypeLabel() + "(" + dao.getTypeId() + ")");
			System.out.print(dao.getLeftRoleTypeDAO().getTypeLabel() + "(" + dao.getLeftRoleTypeDAO().getTypeId() + ") : ");
			System.out.print(dao.getLeftPlayerTypeDAO().getTypeLabel() + "(" + dao.getLeftPlayerTypeDAO().getTypeId() + ") , ");
			System.out.print(dao.getRightRoleTypeDAO().getTypeLabel() + "(" + dao.getRightRoleTypeDAO().getTypeId() + ") : ");
			System.out.println(dao.getRightPlayerTypeDAO().getTypeLabel() + "(" + dao.getRightPlayerTypeDAO().getTypeId() + ") ) ");
		}

	}
}
