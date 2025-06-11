package com.sourcesense.smart_event_platform.mapper;

import com.sourcesense.smart_event_platform.model.Reservation;
import com.sourcesense.smart_event_platform.model.dto.ReservationDto;
import com.sourcesense.smart_event_platform.model.dto.request.InsertReservationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(source = "eventId", target = "eventId")
    Reservation fromInsertRequestToModel(InsertReservationRequest request);

    ReservationDto fromModelToDto(Reservation reservation);

    List<ReservationDto> fromListOfModelToDto(List<Reservation> reservations);


}
