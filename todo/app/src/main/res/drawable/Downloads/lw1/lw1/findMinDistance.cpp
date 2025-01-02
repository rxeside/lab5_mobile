#include "findMinDistance.h"
#include <algorithm>
#include <vector>

std::vector<int> findMinDistance(std::vector<int>& v, std::vector<std::vector<int>>& length, std::vector<std::vector<int>>& weight)
{
	std::sort(v.begin(), v.end());
	if (length.empty())
	{
		std::vector<int> emptyVector = {};
		return emptyVector;
	}
	if (weight.empty())
	{
		std::vector<int> emptyVector = {};
		return emptyVector;
	}
	std::vector<int> minV = {};
	int minSum = 100000000;
	do
	{
		int sum = 0;
		for (int i = 0; i < v.size(); i++)
		{
			for (int j = 0; j < v.size(); j++)
			{
				sum += length[i][j] * weight[v[i]][v[j]];
			};
		}

		if (minSum > sum) {
			minSum = sum;
			minV = v;
		}
	} while (std::next_permutation(v.begin(), v.end()));
	return minV;
}