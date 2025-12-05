(Snails vs Sharks)
-------------------------------------------------------
Пакеты:
  sim.model  - сущности: Player, Ball, GameEntity, Movable, Position
  sim.engine - движок матча: Team, MatchContext, MatchEngine, Stats, EventRecord, TeamType
  sim.ui     - консольный рендер ASCII-поля
  sim.exceptions - проверяемое и непроверяемое исключения
  sim.app    - точка входа Main

Сборка и запуск:
  javac -d out src/sim/app/Main.java src/sim/model/*.java src/sim/engine/*.java src/sim/ui/*.java src/sim/exceptions/*.java
  java -cp out sim.app.Main

Особенности:
 - соответствие SOLID (разделение на пакеты/классы, инкапсуляция, интерфейсы, наследование)
 - используются: abstract class, interface, enum, record, ArrayList, checked exception, unchecked exception
 - equals/hashCode/toString переопределены в ключевых классах
 - матч длится 300 секунд (5 минут); пауза/просмотр истории по Enter
