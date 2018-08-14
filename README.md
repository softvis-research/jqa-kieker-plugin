# jqa-kieker-plugin
This is a stack trace parser of jQAssistant. It enables jQAssistant to scan and to analyze Kieker trace logs.

The plugin currently supports all 46 types of Kieker trace logs in the "kieker/kieker-common/src-gen/kieker/common/record/" folder, as of 14.08.18.
Due to the fact that custom record types can be defined with their own individual properties, they are not supported.

IMPORTANT:
Before the Plugin can be used, the "MonitoringRecordTypes.xml" file has to be copied to the "jqassistant-commandline-neo4jv3-x.x.x\bin\" folder.
