The service provides POST "find" endpoint which gets Int array and a target value to determine whether there exist two distinct elements in the array whose sum equals the target.
Input:
{data: Array[Int], target: Option[Int]}
Output:
[{indexes: List[Int], values: List[Int]}]
The service provides API throttling feature - limiting the number of API requests a user can make in a certain period.
In order to make a docker image: sbt docker:publishLocal (see install.sh)
In order to try and investigate API: [protocol://host:port]/api-docs/#/default/route

