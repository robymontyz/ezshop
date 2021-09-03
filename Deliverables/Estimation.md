# Project Estimation

Authors: Roberto A., Michelangelo B., Gianvito M., Roberto T.

Date: 30/04/2021

Version: 1.0

# Contents
- [Estimate by product decomposition](#estimate-by-product-decomposition)
- [Estimate by activity decomposition](#estimate-by-activity-decomposition)

# Estimation approach

## Estimate by product decomposition

###
|                                                                                                        | Estimate                                 |
| ------------------------------------------------------------------------------------------------------ | ---------------------------------------- |
| NC =  Estimated number of classes to be developed                                                      | 12                                       |
| A = Estimated average size per class, in LOC                                                           | 200 LOC                                  |
| S = Estimated size of project, in LOC (= NC * A)                                                       | 2400 LOC                                 |
| E = Estimated effort, in person hours (here use productivity 10 LOC per person hour)                   | 240 Person Hours                         |
| C = Estimated cost, in euro (here use 1 person hour cost = 30 euro)                                    | 7200 €                                   |
| Estimated calendar time, in calendar weeks (Assume team of 4 people, 8 hours per day, 5 days per week) | 240/(4*8) = 7.5 days = 1weeks + 2.5days  |

## Estimate by activity decomposition

###
|      Activity name                    |  Estimated effort (person hours)  |             
| ------------------------------------- | --------------------------------- | 
| Analysis of requirements              | 12                                |
| Requirements document                 | 36                                |
| GUI design                            | 8                                 |
| Planning and work assignments         | 4                                 |
| Analysis of system design             | 12                                |
| Design document                       | 40                                |
| Brainstorming                         | 2                                 |
| Coding                                | 42                                |
| Database creation                     | 10                                |
| Testing and bug fixing                | 36                                |
| Validation and Integration            | 20                                |
| Deployment                            | 4                                 |
| End project self-evaluation           | 4                                 |
| Risk management                       | 10                                |
| **Total**                             | 240                               |

### Gantt Diagram

```plantuml
@startgantt
scale 1.5
printscale monthly
<style>
ganttDiagram {
    note {
		FontColor Black
		FontSize 9
        FontStyle bold
        BackGroundColor Bisque
		LineColor Black
	}
}
</style>
-- Start project--

[Analysis of requirements (12 person hours)] lasts 1 days
[Requirements document (20 + 16 person hours)] lasts 2 days
[GUI design (8 person hours)] lasts 1 days
[Planning and work assignments (4 person hours)] lasts 1 days

[Analysis of system design(4 + 8 person hours)] lasts 2 days
[Analysis of requirements (12 person hours)] -[#FFFFFF00]-> [GUI design (8 person hours)]
[Analysis of requirements (12 person hours)] -[#FFFFFF00]-> [Planning and work assignments (4 person hours)]
[Requirements completed] happens at [Planning and work assignments (4 person hours)]'s end
[Analysis of requirements (12 person hours)] -[#FFFFFF00]-> [Analysis of system design(4 + 8 person hours)]
[Design document (24 + 16 person hours)] lasts 2 days
[Planning and work assignments (4 person hours)] -[#FFFFFF00]-> [Design document (24 + 16 person hours)]
[Brainstorming (2 person hours)] lasts 1 days
[Analysis of system design(4 + 8 person hours)] -[#FFFFFF00]-> [Brainstorming (2 person hours)]
[Coding (14 + 28 person hours)] lasts 2 days
[Design completed] happens at [Brainstorming (2 person hours)]'s end
[Analysis of system design(4 + 8 person hours)] -[#FFFFFF00]-> [Coding (14 + 28 person hours)]
[Database creation (4 + 6 person hours)] lasts 2 days
[Brainstorming (2 person hours)] -[#FFFFFF00]-> [Database creation (4 + 6 person hours)]
[Testing and bug fixing (26 + 10 person hours)] lasts 2 days
[Coding (14 + 28 person hours)] -[#FFFFFF00]-> [Testing and bug fixing (26 + 10 person hours)]
[Coding and Testing completed] happens at [Testing and bug fixing (26 + 10 person hours)]'s end
[Validation and Integration (20 person hours)] lasts 1 days
[Database creation (4 + 6 person hours)] -[#FFFFFF00]-> [Validation and Integration (20 person hours)]
[Integration and GUI Testing completed] happens at [Validation and Integration (20 person hours)]'s end
[Deployment (2 + 2 person hours)] lasts 2 days
[Database creation (4 + 6 person hours)] -[#FFFFFF00]-> [Deployment (2 + 2 person hours)]
[End project self-evaluation (4 person hours)] lasts 1 days
[Validation and Integration (20 person hours)] -[#FFFFFF00]-> [End project self-evaluation (4 person hours)] 
[Risk management (10 person hours)] lasts 1 days
[Validation and Integration (20 person hours)] -[#FFFFFF00]-> [Risk management (10 person hours)]
-- End project --
[ ] lasts 25 days
[ ] is colored in #FFFFFF00
note bottom
  The days are 32 person hours long
  (4 people working for 8 hours).
end note
@endgantt
```
