# Unreleased
- Improved compatibility with other crosshair mods for forge build (thanks to Fealtous)
- Fixed incorrect behavior of setXRot and setYRot API methods

# 5.0.3
- Updated traditional and simplified Chinese translations (thanks to Xinyang-Gao)

# 5.0.2
- Improved mod-compatibility

# 5.0.1
- Updated Russian translations (thanks to mpustovoi)

# 5.0.0
- Added config option to control player transparency effect when climbing (disabled by default)
- Changed default keybind for swapping shoulders from 'O' to 'U'
- Enabled config option "turn_player_x_rot_with_camera" by default
- Enabled config option "turn_player_y_rot_with_camera" by default
- Disabled config option "turn_with_player" by default
- The player no longer turns with the camera when "turn_with_player" is enabled
- The player now turns with the camera when 'pick_vector' is 'PLAYER' and 'crosshair_type' is 'DYNAMIC'
- Fixed eye height being too low when flying in creative
- Other optimizations and stability fixes
- Reorganized several config options. The following mapping can be used for migration:
    - camera.fov_override_enabled → camera.fov.fov_override_enabled
    - camera.fov_override → camera.fov.fov_override
    - camera.dynamically_adjust_offsets → camera.offset.dynamic_offsets
    - camera.camera_step_size → camera.offset.step_size
    - camera.follow_player_rotations → camera.turn_with_player
    - camera.follow_player_rotations_delay → camera.turn_with_player_delay
    - crosshair.obstruction.show_obstruction_indicator → crosshair.obstruction.obstruction_indicator
    - player.adjust_player_transparency → player.transparency.adjust_transparency
    - player.player_x_rot_follows_camera → player.turning.turn_player_x_rot_with_camera
    - player.player_y_rot_follows_camera → player.turning.turn_player_y_rot_with_camera
    - player.player_y_rot_follow_angle_limit → player.turning.turn_player_y_rot_angle_limit
    - player.turn_player_transparent_when_aiming → player.transparency.when_aiming
- Valkyrien Skies >2.4.11 is no longer marked as incompatible
- API: Replaced callback system with an event system
- API: It is now possible to register multiple plugin classes per mod
- API: The API was restructured and some parts were renamed. A migration guide can be found in the wiki: https://github.com/Exopandora/ShoulderSurfing/wiki/API-Documentation-v5-Migration
