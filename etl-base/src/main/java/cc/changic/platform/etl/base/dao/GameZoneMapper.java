package cc.changic.platform.etl.base.dao;


import cc.changic.platform.etl.base.model.db.GameZone;

import java.util.List;

public interface GameZoneMapper {

    List<GameZone> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table db_etl_server_0001.t_c_game_zone
     *
     * @mbggenerated Thu Jan 29 21:23:22 CST 2015
     */
    int insert(GameZone record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table db_etl_server_0001.t_c_game_zone
     *
     * @mbggenerated Thu Jan 29 21:23:22 CST 2015
     */
    int insertSelective(GameZone record);
}