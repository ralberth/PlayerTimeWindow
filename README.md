# Config File

Rules:
1. If a player is not listed in `schedules`, or is listed in `schedules`
with no entries, they can login anytime.
1. If a player is in "schedules", their day-of-the-week entries
list the times they can login ("opt-in" semantics):
   1. If they have no entry for a day of the week, they cannot login at all
      on that day of the week.
   1. If a day of the week isn't `all` it is parsed as a whitespace-separated
      list of hour ranges using a 24-hour clock.  Each entry is the lower-bound
      hour, a hyphen ("`-`") and the upper-bound hour. The player can login as
      long as the current time is within any of the time ranges.

As a special case, `0-23` means "anytime".

Examples:

```yaml
schedules:
    Player1:
    Player2:
       Sat: 0-24
       Sun: 0-24
    Player3:
       Mon: 9-11 17-21
       Wed: 9-11 17-21
       Fri: 9-11
```

Interpretation:

* `Player0` isn't listed and can login anytime on any day.
* `Player1` **is** listed but has no days of the week.  They can login anytime.
* `Player2` can login anytime on Saturday and Sunday only (no weekdays).
* `Player3` can login Monday between 9 and 11 and also between 17 and 21
  *(9 AM to 11 AM and 5 PM to 9 PM)*.


## TO DO

1. Allow minutes instead of just hour ranges in config file
1. Localize broadcast messages
