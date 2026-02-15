package it.hackhub.infrastructure.persistence;

import it.hackhub.core.entities.core.Hackathon;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Storage in memoria per le entità – iterazione 0: solo Hackathon.
 */
public class StorageInMemoria {

  private final Map<Long, Hackathon> hackathons = new ConcurrentHashMap<>();
  private final AtomicLong nextHackathonId = new AtomicLong(1);

  public Map<Long, Hackathon> getHackathons() {
    return hackathons;
  }

  public long nextHackathonId() {
    return nextHackathonId.getAndIncrement();
  }
}
