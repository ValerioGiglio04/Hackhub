-- =============================================================================
-- Dati di test HackHub
-- Eseguire su DB con schema già creato (avviare prima l'applicazione con ddl-auto=update).
-- Eseguire preferibilmente su DB vuoto (dopo aver eliminato i dati esistenti se necessario).
--
-- La password di test per tutti gli utenti è: test123
-- L'hash è generato con BCrypt (compatibile con Spring Security PasswordEncoder).
-- Esempio: https://www.devglan.com/online-tools/bcrypt-hash-generator
-- =============================================================================
SET @passwordHash = '$2a$12$ROv1pkWNZfOeQTKIYuKMf.xL169dRkTydIS6vfiau5BW3Y1lZtRUO';

-- -----------------------------------------------------------------------------
-- 1. UTENTI (ordine: organizzatore, mentore1, mentore2, giudice1, giudice2, capoteam1, membro1_1, membro1_2, capoteam2, membro2_1 -> id 1..10)
-- -----------------------------------------------------------------------------
INSERT INTO Utenti (email, password_hash, nome, cognome, data_registrazione, ruolo)
VALUES
  ('organizzatore@hackhub.com', @passwordHash, 'Mario', 'Rossi', CURRENT_DATE, 'ORGANIZZATORE'),
  ('mentore1@hackhub.com', @passwordHash, 'Laura', 'Bianchi', CURRENT_DATE, 'MENTORE'),
  ('mentore2@hackhub.com', @passwordHash, 'Giulia', 'Verdi', CURRENT_DATE, 'MENTORE'),
  ('giudice1@hackhub.com', @passwordHash, 'Paolo', 'Neri', CURRENT_DATE, 'GIUDICE'),
  ('giudice2@hackhub.com', @passwordHash, 'Anna', 'Galli', CURRENT_DATE, 'GIUDICE'),
  ('capoteam1@hackhub.com', @passwordHash, 'Marco', 'Ferrari', CURRENT_DATE, 'AUTENTICATO'),
  ('membro1_1@hackhub.com', @passwordHash, 'Sara', 'Romano', CURRENT_DATE, 'AUTENTICATO'),
  ('membro1_2@hackhub.com', @passwordHash, 'Luca', 'Conti', CURRENT_DATE, 'AUTENTICATO'),
  ('capoteam2@hackhub.com', @passwordHash, 'Francesca', 'Esposito', CURRENT_DATE, 'AUTENTICATO'),
  ('membro2_1@hackhub.com', @passwordHash, 'Davide', 'Marini', CURRENT_DATE, 'AUTENTICATO');

-- -----------------------------------------------------------------------------
-- 2. TEAMS (CodeCrushers capo=6, TechInnovators capo=9; email PayPal da application.properties in sandbox)
-- -----------------------------------------------------------------------------
INSERT INTO Teams (nome, email_paypal, id_capo)
VALUES
  ('CodeCrushers', 'valerio.giglio@studenti.unicam.it', 6),
  ('TechInnovators', 'valerio.giglio@studenti.unicam.it', 9);

-- Team_Membri: team 1 -> membri 7, 8; team 2 -> membro 10
INSERT INTO Team_Membri (id_team, id_utente) VALUES (1, 7), (1, 8), (2, 10);

-- -----------------------------------------------------------------------------
-- 3. HACKATHON (date relative a CURRENT_DATE)
-- -----------------------------------------------------------------------------
INSERT INTO Hackathons (
  nome, regolamento, stato,
  inizio_iscrizioni, scadenza_iscrizioni, data_inizio, data_fine, scadenza_sottomissioni,
  luogo, premio, max_team_size
)
VALUES (
  'HackHub Innovation Challenge 2024',
  'Regolamento ufficiale dell''HackHub Innovation Challenge. Le squadre devono sviluppare soluzioni innovative per problemi reali utilizzando tecnologie emergenti.',
  'IN_ISCRIZIONE',
  CAST(DATEADD('DAY', -1, CURRENT_DATE) AS TIMESTAMP),
  DATEADD('MINUTE', 59, DATEADD('HOUR', 23, CAST(DATEADD('DAY', 7, CURRENT_DATE) AS TIMESTAMP))),
  CAST(DATEADD('DAY', 14, CURRENT_DATE) AS TIMESTAMP),
  DATEADD('MINUTE', 59, DATEADD('HOUR', 23, CAST(DATEADD('DAY', 16, CURRENT_DATE) AS TIMESTAMP))),
  DATEADD('MINUTE', 59, DATEADD('HOUR', 23, CAST(DATEADD('DAY', 15, CURRENT_DATE) AS TIMESTAMP))),
  'Politecnico di Milano - Campus Leonardo',
  500.0,
  4
);

-- -----------------------------------------------------------------------------
-- 4. STAFF HACKATHON (organizzatore 1, mentori 2-3, giudici 4-5)
-- -----------------------------------------------------------------------------
INSERT INTO Staff_Hackathon (id_hackathon, id_utente)
VALUES (1, 1), (1, 2), (1, 3), (1, 4), (1, 5);

-- -----------------------------------------------------------------------------
-- 5. ISCRIZIONI TEAM -> HACKATHON (team 1 e 2 iscritti all'hackathon 1)
-- -----------------------------------------------------------------------------
INSERT INTO Iscrizioni_Team_Hackathon (id_team, id_hackathon, data_iscrizione)
VALUES
  (1, 1, CURRENT_TIMESTAMP),
  (2, 1, CURRENT_TIMESTAMP);

-- -----------------------------------------------------------------------------
-- 6. SOTTOMISSIONI
-- -----------------------------------------------------------------------------
INSERT INTO Sottomissioni (id_team, id_hackathon, link_progetto, data_caricamento)
VALUES
  (1, 1, 'https://github.com/codecrushers/hackhub-innovation-2024', CURRENT_TIMESTAMP),
  (2, 1, 'https://github.com/techinnovators/smart-solution-hackathon', CURRENT_TIMESTAMP);
