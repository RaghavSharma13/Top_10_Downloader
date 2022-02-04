package learnprogramming.academy

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

class ParseApplicationData {

    val applicationData = ArrayList<FeedEntry>()

    fun parse(xmlData: String): Boolean{
        var status = true
        var inEntryTag = false
        var textValue = "" //in tags

        try{
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(xmlData.reader())
            var eventType = xpp.eventType
            var currentRecord = FeedEntry()
            while(eventType !=XmlPullParser.END_DOCUMENT){
                val tagName = xpp.name?.lowercase() // we use safe call operator(?) here because xpp.name initially is null
                // and lowercase() can't be called on null
                when(eventType){
                    XmlPullParser.START_TAG -> if(tagName == "entry")  inEntryTag = true
                    XmlPullParser.TEXT -> textValue=xpp.text
                    XmlPullParser.END_TAG->{
                        if(inEntryTag){
                            when(tagName){
                                "entry" ->{
                                    applicationData.add(currentRecord)
                                    inEntryTag = false
                                    currentRecord = FeedEntry()
                                }
                                "name"-> currentRecord.name=textValue
                                "artist"->currentRecord.artist=textValue
                                "releasedate"-> currentRecord.releaseDate=textValue
                                "summary"-> currentRecord.summary=textValue
                                "image"-> currentRecord.imageURL=textValue
                            }
                        }
                    }
                }
                eventType = xpp.next()
            }
        } catch (e: Exception){
            e.printStackTrace()
            status = false
        }
        return status
    }

}