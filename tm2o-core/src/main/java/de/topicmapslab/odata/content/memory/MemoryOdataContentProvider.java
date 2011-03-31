package de.topicmapslab.odata.content.memory;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapix.io.CTMTopicMapReader;
import org.tmapix.io.LTMTopicMapReader;
import org.tmapix.io.TopicMapReader;
import org.tmapix.io.XTMTopicMapReader;

import de.topicmapslab.format_estimator.FormatEstimator;
import de.topicmapslab.odata.content.TmqlOdataContentProviderImpl;
import de.topicmapslab.odata.exception.TopicMapsODataException;

/**
 * Content provider using a in memory topic map source running in a MaJorToM topic maps engine
 * 
 * @author Sven Krosse
 */
public class MemoryOdataContentProvider extends TmqlOdataContentProviderImpl<MemoryTmqlHelper> {

	/**
	 * the TMQL helper
	 */
	private MemoryTmqlHelper helper;

	/**
	 * constructor
	 * 
	 * @param topicMapId
	 *            the topic map id
	 */
	public MemoryOdataContentProvider(final String topicMapId) {
		super(topicMapId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize(String namespace, Properties properties) throws TopicMapsODataException {
		/*
		 * load properties
		 */
		Object oPath = properties.get("path");
		Object oFile = properties.get("file");
		if (oPath == null && oFile == null) {
			throw new TopicMapsODataException("Missing required properties");
		}
		/*
		 * load from file
		 */
		try {
			/*
			 * create topic map
			 */
			TopicMap topicMap = TopicMapSystemFactory.newInstance().newTopicMapSystem().createTopicMap("http://local.tm2o.topicmap.de");
			/*
			 * get topic map reader by format
			 */
			TopicMapReader topicMapReader;
			File file = null;
			if (oPath != null) {
				file = new File(oPath.toString());
			} else {
				file = (File) oFile;
			}
			FileReader reader = new FileReader(file);
			switch (FormatEstimator.guessFormat(reader)) {
				case CTM:
				case CTM_1_0: {
					topicMapReader = new CTMTopicMapReader(topicMap, file);
				}
					break;
				case XTM_1_0:
				case XTM_1_1:
				case XTM_2_0:
				case XTM_2_1: {
					topicMapReader = new XTMTopicMapReader(topicMap, file);
				}
					break;
				case LTM:
				case LTM_1_0:
				case LTM_1_1:
				case LTM_1_2:
				case LTM_1_3: {
					topicMapReader = new LTMTopicMapReader(topicMap, file);
				}
					break;
				default: {
					throw new TopicMapsODataException("Unsupported topic map file format!");
				}
			}
			/*
			 * load topic map
			 */
			topicMapReader.read();
			/*
			 * create memory helper
			 */
			helper = new MemoryTmqlHelper(topicMap);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		super.initialize(namespace, properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MemoryTmqlHelper getTmqlHelper() {
		return helper;
	}

}
