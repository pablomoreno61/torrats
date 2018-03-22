package es.ibrands.torrats.util;

import es.ibrands.torrats.model.CalendarList;
import io.reactivex.Observable;

import retrofit2.http.GET;

public interface TorratsApiInterface
{
    @GET("events/")
    Observable<CalendarList> getList();
}
