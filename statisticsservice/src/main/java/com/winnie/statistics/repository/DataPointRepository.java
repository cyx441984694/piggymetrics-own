package com.winnie.statistics.repository;


import com.winnie.statistics.domain.timeseries.DataPoint;
import com.winnie.statistics.domain.timeseries.DataPointId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataPointRepository extends CrudRepository<DataPoint, DataPointId> {

    List<DataPoint> findByIdAccount(String acocunt);


}
