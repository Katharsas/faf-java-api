package com.faforever.api.event;

import com.faforever.api.data.domain.Event;
import com.faforever.api.data.domain.PlayerEvent;
import com.google.common.base.MoreObjects;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.function.BiFunction;

@Service
public class EventsService {

  private final EventRepository eventRepository;
  private final PlayerEventRepository playerEventRepository;

  @Inject
  public EventsService(EventRepository eventRepository, PlayerEventRepository playerEventRepository) {
    this.eventRepository = eventRepository;
    this.playerEventRepository = playerEventRepository;
  }

  UpdatedEventResponse increment(int playerId, String eventId, int steps) {
    BiFunction<Integer, Integer, Integer> stepsFunction = (currentSteps, newSteps) -> currentSteps + newSteps;
    Event event = eventRepository.getOne(eventId);

    PlayerEvent playerEvent = getOrCreatePlayerEvent(playerId, event);

    int currentSteps1 = MoreObjects.firstNonNull(playerEvent.getCurrentCount(), 0);
    int newCurrentCount = stepsFunction.apply(currentSteps1, steps);

    playerEvent.setCurrentCount(newCurrentCount);
    playerEventRepository.save(playerEvent);

    return new UpdatedEventResponse(eventId, newCurrentCount);
  }

  private PlayerEvent getOrCreatePlayerEvent(int playerId, Event event) {
    return playerEventRepository.findOneByEventIdAndPlayerId(event.getId(), playerId)
      .orElseGet(() -> new PlayerEvent()
        .setPlayerId(playerId)
        .setEvent(event));
  }
}
