#TwitterCore SDK

## Scribing
TwitterCore provides kit developers the ability to scribe events for tracking through Twitter analytics, such as user impressions.

To scribe an event:

```
final String eventCategory = "client_event";
final EventNamespace eventNamespace = new EventNamespace.Builder()
        .setClient("client " + tweet.getId())
        .setPage("page " + tweet.getId())
        .setSection("section " + tweet.getId())
        .setComponent("component " + tweet.getId())
        .setElement("element " + tweet.getId())
        .setAction("action " + tweet.getId())
        .builder();
TweetUi.getInstance().scribe(new ScribeEvent(eventCategory,
        eventNamespace, System.currentTimeMillis()));
```

For more information on scribing in general, see:

* [A guide to mobile client event scribing](https://confluence.twitter.biz/display/CES/A+guide+to+mobile+client_event+scribing)
* [Echidna Client Event Testing Overview](https://confluence.twitter.biz/display/ANALYTICS/Echidna+Client+Event+Testing+Overview)
