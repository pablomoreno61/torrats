package es.ibrands.torrats.util;

import es.ibrands.torrats.model.CalendarEvent;

import es.ibrands.torrats.model.CalendarList;
import io.reactivex.Observable;

import java.util.List;

import retrofit2.http.GET;

public interface TorratsApiInterface
{
    @GET("events/")
    // Observable<List<CalendarEvent>> getList();
    Observable<CalendarList> getList();
}
