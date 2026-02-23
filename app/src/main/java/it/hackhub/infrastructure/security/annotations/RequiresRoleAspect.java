package it.hackhub.infrastructure.security.annotations;

import it.hackhub.application.exceptions.UnauthorizedException;
import it.hackhub.core.entities.core.Utente;
import it.hackhub.application.repositories.associations.StaffHackatonRepository;
import it.hackhub.application.repositories.core.HackathonRepository;
import it.hackhub.application.repositories.core.TeamRepository;
import it.hackhub.application.repositories.core.UtenteRepository;
import it.hackhub.infrastructure.security.AuthorizationUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;

/**
 * Aspect che intercetta @RequiresRole e @AuthenticatedUser ed esegue i controlli di autorizzazione (RBAC).
 */
@Aspect
@Component
public class RequiresRoleAspect {

  @Autowired
  private UtenteRepository utenteRepository;

  @Autowired
  private HackathonRepository hackathonRepository;

  @Autowired
  private TeamRepository teamRepository;

  @Autowired
  private StaffHackatonRepository staffHackatonRepository;

  @Autowired
  private ParameterNameDiscoverer parameterNameDiscoverer;

  @Before("@annotation(requiresRole)")
  public void checkRequiresRole(JoinPoint joinPoint, RequiresRole requiresRole) {
    Utente currentUser = AuthorizationUtils.getCurrentUser(utenteRepository);
    checkUserRole(currentUser, requiresRole.role());

    if (requiresRole.requiresHackathonAssignment()) {
      checkHackathonAssignment(joinPoint, currentUser, requiresRole.role());
    }
    if (requiresRole.requiresTeamMember()) {
      AuthorizationUtils.requireTeamMember(currentUser, extractTeamId(joinPoint), teamRepository);
    }
    if (requiresRole.requiresTeamLeader()) {
      AuthorizationUtils.requireTeamLeader(currentUser, extractTeamId(joinPoint), teamRepository);
    }
  }

  @Before("@within(authenticatedUser) || @annotation(authenticatedUser)")
  public void checkAuthenticatedUser(JoinPoint joinPoint, AuthenticatedUser authenticatedUser) {
    AuthorizationUtils.getCurrentUser(utenteRepository);
  }

  @Before("@within(requiresRole)")
  public void checkClassLevelRequiresRole(JoinPoint joinPoint, RequiresRole requiresRole) {
    if (!hasMethodLevelAnnotation(joinPoint, RequiresRole.class)) {
      checkRequiresRole(joinPoint, requiresRole);
    }
  }

  private boolean hasMethodLevelAnnotation(JoinPoint joinPoint, Class<?> annotationClass) {
    try {
      MethodSignature signature = (MethodSignature) joinPoint.getSignature();
      return signature.getMethod().isAnnotationPresent(annotationClass.asSubclass(java.lang.annotation.Annotation.class));
    } catch (Exception e) {
      return false;
    }
  }

  private void checkUserRole(Utente user, Utente.RuoloStaff requiredRole) {
    switch (requiredRole) {
      case ORGANIZZATORE:
        AuthorizationUtils.requireOrganizzatore(user);
        break;
      case GIUDICE:
        if (user.getRuolo() != Utente.RuoloStaff.GIUDICE) {
          throw new UnauthorizedException("Solo i giudici possono eseguire questa operazione.");
        }
        break;
      case MENTORE:
        if (user.getRuolo() != Utente.RuoloStaff.MENTORE) {
          throw new UnauthorizedException("Solo i mentori possono eseguire questa operazione.");
        }
        break;
      case AUTENTICATO:
        break;
      default:
        throw new UnauthorizedException("Ruolo non riconosciuto: " + requiredRole);
    }
  }

  private void checkHackathonAssignment(JoinPoint joinPoint, Utente user, Utente.RuoloStaff role) {
    Long hackathonId = extractHackathonId(joinPoint);
    if (hackathonId == null) {
      throw new IllegalArgumentException("ID hackathon richiesto per la verifica dell'assegnazione");
    }
    if (role == Utente.RuoloStaff.GIUDICE) {
      AuthorizationUtils.requireGiudiceOfHackathon(user, hackathonId, hackathonRepository, staffHackatonRepository);
      return;
    }
    if (role == Utente.RuoloStaff.MENTORE) {
      AuthorizationUtils.requireMentoreOfHackathon(user, hackathonId, hackathonRepository, staffHackatonRepository);
      return;
    }
    if (role == Utente.RuoloStaff.ORGANIZZATORE) {
      AuthorizationUtils.requireOrganizzatoreOfHackathon(user, hackathonId, hackathonRepository, staffHackatonRepository);
    }
  }

  private Long extractHackathonId(JoinPoint joinPoint) {
    Object[] args = joinPoint.getArgs();
    String[] paramNames = getParameterNames(joinPoint);
    for (int i = 0; i < args.length; i++) {
      if (args[i] instanceof Long) {
        String paramName = paramNames != null && i < paramNames.length ? paramNames[i] : "";
        if ("hackathonId".equals(paramName) || "id".equals(paramName)) {
          return (Long) args[i];
        }
      }
      if (args[i] != null) {
        try {
          Object hackathonId = args[i].getClass().getMethod("getHackathonId").invoke(args[i]);
          if (hackathonId instanceof Long) return (Long) hackathonId;
        } catch (Exception e) { /* ignore */ }
      }
    }
    return null;
  }

  private Long extractTeamId(JoinPoint joinPoint) {
    Object[] args = joinPoint.getArgs();
    String[] paramNames = getParameterNames(joinPoint);
    for (int i = 0; i < args.length; i++) {
      if (args[i] instanceof Long) {
        String paramName = paramNames != null && i < paramNames.length ? paramNames[i] : "";
        if ("teamId".equals(paramName) || ("id".equals(paramName) && !containsHackathonIdParam(paramNames))) {
          return (Long) args[i];
        }
      }
      if (args[i] != null) {
        try {
          Object teamId = args[i].getClass().getMethod("getTeamId").invoke(args[i]);
          if (teamId instanceof Long) return (Long) teamId;
        } catch (Exception e) { /* ignore */ }
      }
    }
    return null;
  }

  private String[] getParameterNames(JoinPoint joinPoint) {
    try {
      MethodSignature signature = (MethodSignature) joinPoint.getSignature();
      return parameterNameDiscoverer.getParameterNames(signature.getMethod());
    } catch (Exception e) {
      return null;
    }
  }

  private boolean containsHackathonIdParam(String[] paramNames) {
    if (paramNames == null) return false;
    for (String name : paramNames) {
      if ("hackathonId".equals(name)) return true;
    }
    return false;
  }
}
