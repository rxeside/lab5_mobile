#include <algorithm>
#include <iostream>
#include <vector>
#define BOOST_TIMER_ENABLE_DEPRECATED
#include <boost/timer.hpp>
#include <string>
#include "findMinDistance.h"

int main()
{	
	std::vector<int> v = { 0, 1, 2, 3 };
	std::vector<std::vector<int>> length = {{0, 2, 3, 4}, 
											{2, 0, 5, 1}, 
											{3, 5, 0, 4}, 
											{4, 1, 4, 0}};
	std::vector<std::vector<int>> weight = {{0, 13, 10, 2},
											{13, 0, 12, 4},
											{10, 12, 0, 8},
											{2, 4, 8, 0}};
	std::vector<int> minV = findMinDistance(v, length, weight);
	copy(minV.begin(), minV.end(), std::ostream_iterator<int>(std::cout, " "));
}