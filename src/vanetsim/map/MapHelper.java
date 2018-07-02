package vanetsim.map;

import vanetsim.scenario.Vehicle;

public final class MapHelper {

    public static double calculateDistancePointToStreet(Street street, int x, int y, boolean sqrt, int[] result) {
        if (findNearestPointOnStreet(street, x, y, result)) {
            long tmp1 = (long) result[0] - x;
            long tmp2 = (long) result[1] - y;
            if (sqrt)
                return Math.sqrt(tmp1 * tmp1 + tmp2 * tmp2);
            else
                return (tmp1 * tmp1 + tmp2 * tmp2);
        } else
            return Double.MAX_VALUE;
    }

    public static boolean findNearestPointOnStreet(Street street, int x, int y, int[] result) {
        if (result.length == 2) {
            int p1_x = street.getStartNode().getX();
            int p1_y = street.getStartNode().getY();
            int p2_x = street.getEndNode().getX();
            int p2_y = street.getEndNode().getY();
            long tmp1 = p2_x - p1_x;
            long tmp2 = p2_y - p1_y;
            long tmp3 = (tmp1 * tmp1 + tmp2 * tmp2);
            if (tmp3 != 0) {
                double u = (((double) x - p1_x) * ((double) p2_x - p1_x) + ((double) y - p1_y) * ((double) p2_y - p1_y)) / tmp3;
                if (u >= 1.0) {
                    result[0] = p2_x;
                    result[1] = p2_y;
                } else if (u <= 0.0) {
                    result[0] = p1_x;
                    result[1] = p1_y;
                } else {
                    double tmp4 = p1_x + u * tmp1;
                    result[0] = (int) (tmp4 + 0.5);
                    tmp4 = p1_y + u * tmp2;
                    result[1] = (int) (tmp4 + 0.5);
                }
            } else {
                result[0] = p1_x;
                result[1] = p1_y;
            }
            return true;
        } else
            return false;
    }

    public static Street findNearestStreet(int x, int y, int maxDistance, double[] distance, int[] nearestPoint) {
        Map map = Map.getInstance();
        Region[][] Regions = map.getRegions();
        if (Regions != null && nearestPoint.length > 1 && distance.length > 0) {
            int mapMinX, mapMinY, mapMaxX, mapMaxY, regionMinX, regionMinY, regionMaxX, regionMaxY;
            int[] tmpPoint = new int[2];
            Street[] streets;
            int i, j, k, size;
            Street bestStreet = null;
            double tmpDistance, bestDistance = Double.MAX_VALUE;
            long maxDistanceSquared = (long) maxDistance * maxDistance;
            long tmp = x - maxDistance;
            if (tmp < 0)
                mapMinX = 0;
            else if (tmp < Integer.MAX_VALUE)
                mapMinX = (int) tmp;
            else
                mapMinX = Integer.MAX_VALUE;
            tmp = x + (long) maxDistance;
            if (tmp < 0)
                mapMaxX = 0;
            else if (tmp < Integer.MAX_VALUE)
                mapMaxX = (int) tmp;
            else
                mapMaxX = Integer.MAX_VALUE;
            tmp = y - maxDistance;
            if (tmp < 0)
                mapMinY = 0;
            else if (tmp < Integer.MAX_VALUE)
                mapMinY = (int) tmp;
            else
                mapMinY = Integer.MAX_VALUE;
            tmp = y + (long) maxDistance;
            if (tmp < 0)
                mapMaxY = 0;
            else if (tmp < Integer.MAX_VALUE)
                mapMaxY = (int) tmp;
            else
                mapMaxY = Integer.MAX_VALUE;
            Region tmpregion = map.getRegionOfPoint(mapMinX, mapMinY);
            regionMinX = tmpregion.getX();
            regionMinY = tmpregion.getY();
            tmpregion = map.getRegionOfPoint(mapMaxX, mapMaxY);
            regionMaxX = tmpregion.getX();
            regionMaxY = tmpregion.getY();
            for (i = regionMinX; i <= regionMaxX; ++i) {
                for (j = regionMinY; j <= regionMaxY; ++j) {
                    streets = Regions[i][j].getStreets();
                    size = streets.length;
                    for (k = 0; k < size; ++k) {
                        tmpDistance = calculateDistancePointToStreet(streets[k], x, y, false, tmpPoint);
                        if (tmpDistance < maxDistanceSquared && tmpDistance < bestDistance) {
                            bestDistance = tmpDistance;
                            bestStreet = streets[k];
                            nearestPoint[0] = tmpPoint[0];
                            nearestPoint[1] = tmpPoint[1];
                        }
                    }
                }
            }
            distance[0] = bestDistance;
            return bestStreet;
        } else
            return null;
    }

    public static Vehicle findNearestVehicle(int x, int y, int maxDistance, long[] distance) {
        Map map = Map.getInstance();
        Region[][] Regions = map.getRegions();
        if (Regions != null && distance.length > 0) {
            int mapMinX, mapMinY, mapMaxX, mapMaxY, regionMinX, regionMinY, regionMaxX, regionMaxY;
            Vehicle[] vehicles;
            int i, j, k, size;
            long dx, dy;
            Vehicle tmpVehicle, bestVehicle = null;
            long tmpDistance, bestDistance = Long.MAX_VALUE;
            long maxDistanceSquared = (long) maxDistance * maxDistance;
            long tmp = x - maxDistance;
            if (tmp < 0)
                mapMinX = 0;
            else if (tmp < Integer.MAX_VALUE)
                mapMinX = (int) tmp;
            else
                mapMinX = Integer.MAX_VALUE;
            tmp = x + (long) maxDistance;
            if (tmp < 0)
                mapMaxX = 0;
            else if (tmp < Integer.MAX_VALUE)
                mapMaxX = (int) tmp;
            else
                mapMaxX = Integer.MAX_VALUE;
            tmp = y - maxDistance;
            if (tmp < 0)
                mapMinY = 0;
            else if (tmp < Integer.MAX_VALUE)
                mapMinY = (int) tmp;
            else
                mapMinY = Integer.MAX_VALUE;
            tmp = y + (long) maxDistance;
            if (tmp < 0)
                mapMaxY = 0;
            else if (tmp < Integer.MAX_VALUE)
                mapMaxY = (int) tmp;
            else
                mapMaxY = Integer.MAX_VALUE;
            Region tmpregion = map.getRegionOfPoint(mapMinX, mapMinY);
            regionMinX = tmpregion.getX();
            regionMinY = tmpregion.getY();
            tmpregion = map.getRegionOfPoint(mapMaxX, mapMaxY);
            regionMaxX = tmpregion.getX();
            regionMaxY = tmpregion.getY();
            for (i = regionMinX; i <= regionMaxX; ++i) {
                for (j = regionMinY; j <= regionMaxY; ++j) {
                    vehicles = Regions[i][j].getVehicleArray();
                    size = vehicles.length;
                    for (k = 0; k < size; ++k) {
                        tmpVehicle = vehicles[k];
                        if (tmpVehicle.isActive() && tmpVehicle.getX() >= mapMinX && tmpVehicle.getX() <= mapMaxX && tmpVehicle.getY() >= mapMinY && tmpVehicle.getY() <= mapMaxY) {
                            dx = tmpVehicle.getX() - x;
                            dy = tmpVehicle.getY() - y;
                            tmpDistance = dx * dx + dy * dy;
                            if (tmpDistance < maxDistanceSquared && tmpDistance < bestDistance) {
                                bestDistance = tmpDistance;
                                bestVehicle = tmpVehicle;
                            }
                        }
                    }
                }
            }
            distance[0] = bestDistance;
            return bestVehicle;
        } else
            return null;
    }

    public static Node findNearestNode(int x, int y, int maxDistance, long[] distance) {
        Map map = Map.getInstance();
        Region[][] Regions = map.getRegions();
        if (Regions != null && distance.length > 0) {
            int mapMinX, mapMinY, mapMaxX, mapMaxY, regionMinX, regionMinY, regionMaxX, regionMaxY;
            Node[] nodes;
            int i, j, k;
            Node tmpNode, bestNode = null;
            long tmpDistance, bestDistance = Long.MAX_VALUE;
            long maxDistanceSquared = (long) maxDistance * maxDistance;
            long tmp = x - maxDistance;
            if (tmp < 0)
                mapMinX = 0;
            else if (tmp < Integer.MAX_VALUE)
                mapMinX = (int) tmp;
            else
                mapMinX = Integer.MAX_VALUE;
            tmp = x + (long) maxDistance;
            if (tmp < 0)
                mapMaxX = 0;
            else if (tmp < Integer.MAX_VALUE)
                mapMaxX = (int) tmp;
            else
                mapMaxX = Integer.MAX_VALUE;
            tmp = y - maxDistance;
            if (tmp < 0)
                mapMinY = 0;
            else if (tmp < Integer.MAX_VALUE)
                mapMinY = (int) tmp;
            else
                mapMinY = Integer.MAX_VALUE;
            tmp = y + (long) maxDistance;
            if (tmp < 0)
                mapMaxY = 0;
            else if (tmp < Integer.MAX_VALUE)
                mapMaxY = (int) tmp;
            else
                mapMaxY = Integer.MAX_VALUE;
            Region tmpregion = map.getRegionOfPoint(mapMinX, mapMinY);
            regionMinX = tmpregion.getX();
            regionMinY = tmpregion.getY();
            tmpregion = map.getRegionOfPoint(mapMaxX, mapMaxY);
            regionMaxX = tmpregion.getX();
            regionMaxY = tmpregion.getY();
            long dx, dy;
            for (i = regionMinX; i <= regionMaxX; ++i) {
                for (j = regionMinY; j <= regionMaxY; ++j) {
                    nodes = Regions[i][j].getNodes();
                    for (k = 0; k < nodes.length; ++k) {
                        tmpNode = nodes[k];
                        if (tmpNode.getX() >= mapMinX && tmpNode.getX() <= mapMaxX && tmpNode.getY() >= mapMinY && tmpNode.getY() <= mapMaxY) {
                            dx = tmpNode.getX() - x;
                            dy = tmpNode.getY() - y;
                            tmpDistance = dx * dx + dy * dy;
                            if (tmpDistance < maxDistanceSquared && tmpDistance < bestDistance) {
                                bestDistance = tmpDistance;
                                bestNode = tmpNode;
                            }
                        }
                    }
                }
            }
            distance[0] = bestDistance;
            return bestNode;
        } else
            return null;
    }

    public static boolean findSegmentIntersection(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, double[] result) {
        if (result.length == 2) {
            double dx1 = x2 - x1;
            double dx2 = x4 - x3;
            double dy1 = y2 - y1;
            double dy2 = y4 - y3;
            double dx3 = x1 - x3;
            double dy3 = y1 - y3;
            double len1 = Math.sqrt(dx1 * dx1 + dy1 * dy1);
            double len2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);
            if (Math.abs((dx1 * dx2 + dy1 * dy2) / (len1 * len2)) == 1)
                return false;
            double div = dy2 * dx1 - dx2 * dy1;
            double ua = (dx2 * dy3 - dy2 * dx3) / div;
            double resX = x1 + ua * dx1;
            double resY = y1 + ua * dy1;
            dx1 = resX - x1;
            dx2 = resX - x2;
            dy1 = resY - y1;
            dy2 = resY - y2;
            double segmentLen = Math.sqrt(dx1 * dx1 + dy1 * dy1) + Math.sqrt(dx2 * dx2 + dy2 * dy2);
            if (Math.abs(len1 - segmentLen) > 0.01)
                return false;
            dx1 = resX - x3;
            dx2 = resX - x4;
            dy1 = resY - y3;
            dy2 = resY - y4;
            segmentLen = Math.sqrt(dx1 * dx1 + dy1 * dy1) + Math.sqrt(dx2 * dx2 + dy2 * dy2);
            if (Math.abs(len2 - segmentLen) > 0.01)
                return false;
            result[0] = resX;
            result[1] = resY;
            return true;
        } else
            return false;
    }

    public static int findLineCircleIntersections(int circleX, int circleY, int circleRadius, int x1, int y1, int x2, int y2, boolean onlyOnSegment, int[] result) {
        if (result.length == 4) {
            double dx1 = x2 - x1;
            double dy1 = y2 - y1;
            double dx2 = x1 - circleX;
            double dy2 = y1 - circleY;
            double a = dx1 * dx1 + dy1 * dy1;
            double b = 2 * (dx1 * dx2 + dy1 * dy2);
            double c = dx2 * dx2 + dy2 * dy2 - ((double) circleRadius * circleRadius);
            double det = b * b - 4 * a * c;
            if (a <= 0.0000001 || det < 0) {
                return 0;
            } else if (det == 0) {
                double t = -b / (2 * a);
                if (!onlyOnSegment || (t >= 0 && t <= 1)) {
                    result[0] = (int) StrictMath.floor(0.5 + x1 + t * dx1);
                    result[1] = (int) StrictMath.floor(0.5 + y1 + t * dy1);
                    return 1;
                } else
                    return 0;
            } else {
                int found = 0;
                double t = (-b + Math.sqrt(det)) / (2 * a);
                if (!onlyOnSegment || (t >= 0 && t <= 1)) {
                    result[0] = (int) StrictMath.floor(0.5 + x1 + t * dx1);
                    result[1] = (int) StrictMath.floor(0.5 + y1 + t * dy1);
                    found = 1;
                }
                t = (-b - Math.sqrt(det)) / (2 * a);
                if (!onlyOnSegment || (t >= 0 && t <= 1)) {
                    if (found == 0) {
                        result[0] = (int) StrictMath.floor(0.5 + x1 + t * dx1);
                        result[1] = (int) StrictMath.floor(0.5 + y1 + t * dy1);
                        found = 1;
                    } else {
                        result[2] = (int) StrictMath.floor(0.5 + x1 + t * dx1);
                        result[3] = (int) StrictMath.floor(0.5 + y1 + t * dy1);
                        found = 2;
                    }
                }
                return found;
            }
        } else
            return 0;
    }

    public static boolean getXYParallelRight(int x1, int y1, int x2, int y2, int distance, double[] result) {
        if (result.length == 2) {
            int dx = x2 - x1;
            int dy = y2 - y1;
            if (dx == 0) {
                if (dy < 0) {
                    result[0] = distance;
                    result[1] = 0;
                } else {
                    result[0] = -distance;
                    result[1] = 0;
                }
                return true;
            } else if (dy == 0) {
                if (dx > 0) {
                    result[0] = 0;
                    result[1] = distance;
                } else {
                    result[0] = 0;
                    result[1] = -distance;
                }
                return true;
            } else {
                double a = ((double) y1 - y2) / ((double) x1 - x2);
                double a2 = -1.0 / a;
                double b2 = y1 - a2 * x1;
                double endX2 = x1 + 200000.0;
                double endY2 = a2 * endX2 + b2;
                double dx2 = endX2 - x1;
                double dy2 = endY2 - y1;
                double length2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);
                double tmp = distance / length2;
                result[0] = dx2 * tmp;
                result[1] = dy2 * tmp;
                if (dy > 0) {
                    result[0] = -result[0];
                    result[1] = -result[1];
                }
                return true;
            }
        } else
            return false;
    }

    public static void calculateBridges(Street bridgeStreet, Street otherStreet) {
        if (bridgeStreet.getStartNode() != otherStreet.getStartNode() && bridgeStreet.getEndNode() != otherStreet.getEndNode() && bridgeStreet.getStartNode() != otherStreet.getEndNode() && bridgeStreet.getEndNode() != otherStreet.getStartNode()) {
            int width;
            double[] result = new double[2];
            if (bridgeStreet.isOneway())
                width = (bridgeStreet.getLanesCount() * Map.LANE_WIDTH) + 45;
            else
                width = (2 * bridgeStreet.getLanesCount() * Map.LANE_WIDTH) + 45;
            getXYParallelRight(bridgeStreet.getStartNode().getX(), bridgeStreet.getStartNode().getY(), bridgeStreet.getEndNode().getX(), bridgeStreet.getEndNode().getY(), width / 2, result);
            int x1 = (int) Math.round(bridgeStreet.getStartNode().getX() + result[0]);
            int y1 = (int) Math.round(bridgeStreet.getStartNode().getY() + result[1]);
            int x2 = (int) Math.round(bridgeStreet.getEndNode().getX() + result[0]);
            int y2 = (int) Math.round(bridgeStreet.getEndNode().getY() + result[1]);
            int x3 = (int) Math.round(bridgeStreet.getStartNode().getX() - result[0]);
            int y3 = (int) Math.round(bridgeStreet.getStartNode().getY() - result[1]);
            int x4 = (int) Math.round(bridgeStreet.getEndNode().getX() - result[0]);
            int y4 = (int) Math.round(bridgeStreet.getEndNode().getY() - result[1]);
            double[] result2 = new double[2];
            if (otherStreet.isOneway())
                width = (otherStreet.getLanesCount() * Map.LANE_WIDTH) + 45;
            else
                width = (2 * otherStreet.getLanesCount() * Map.LANE_WIDTH) + 45;
            getXYParallelRight(otherStreet.getStartNode().getX(), otherStreet.getStartNode().getY(), otherStreet.getEndNode().getX(), otherStreet.getEndNode().getY(), width / 2, result2);
            int x5 = (int) Math.round(otherStreet.getStartNode().getX() + result2[0]);
            int y5 = (int) Math.round(otherStreet.getStartNode().getY() + result2[1]);
            int x6 = (int) Math.round(otherStreet.getEndNode().getX() + result2[0]);
            int y6 = (int) Math.round(otherStreet.getEndNode().getY() + result2[1]);
            int x7 = (int) Math.round(otherStreet.getStartNode().getX() - result2[0]);
            int y7 = (int) Math.round(otherStreet.getStartNode().getY() - result2[1]);
            int x8 = (int) Math.round(otherStreet.getEndNode().getX() - result2[0]);
            int y8 = (int) Math.round(otherStreet.getEndNode().getY() - result2[1]);
            double[] firstIntersect = new double[2];
            double[] secondIntersect = new double[2];
            double[] thirdIntersect = new double[2];
            double[] fourthIntersect = new double[2];
            int intersectsfound = 0;
            boolean foundFirstIntersect = false, foundSecondIntersect = false, foundThirdIntersect = false, foundFourthIntersect = false;
            if (findSegmentIntersection(x1, y1, x2, y2, x5, y5, x6, y6, firstIntersect)) {
                foundFirstIntersect = true;
                ++intersectsfound;
            }
            if (findSegmentIntersection(x1, y1, x2, y2, x7, y7, x8, y8, secondIntersect)) {
                foundSecondIntersect = true;
                ++intersectsfound;
            }
            if (findSegmentIntersection(x3, y3, x4, y4, x5, y5, x6, y6, thirdIntersect)) {
                foundThirdIntersect = true;
                ++intersectsfound;
            }
            if (findSegmentIntersection(x3, y3, x4, y4, x7, y7, x8, y8, fourthIntersect)) {
                foundFourthIntersect = true;
                ++intersectsfound;
            }
            if (intersectsfound > 0) {
                if (intersectsfound == 3) {
                    if (!foundFirstIntersect) {
                        double dx = thirdIntersect[0] + result[0] - bridgeStreet.getStartNode().getX();
                        double dy = thirdIntersect[1] + result[1] - bridgeStreet.getStartNode().getY();
                        double lengthSquared = dx * dx + dy * dy;
                        dx = thirdIntersect[0] + result[0] - bridgeStreet.getEndNode().getX();
                        dy = thirdIntersect[1] + result[1] - bridgeStreet.getEndNode().getY();
                        if (lengthSquared < width * width * 4) {
                            firstIntersect[0] = bridgeStreet.getStartNode().getX() + result[0];
                            firstIntersect[1] = bridgeStreet.getStartNode().getY() + result[1];
                        } else if ((dx * dx + dy * dy) < width * width * 4) {
                            firstIntersect[0] = bridgeStreet.getEndNode().getX() + result[0];
                            firstIntersect[1] = bridgeStreet.getEndNode().getY() + result[1];
                        } else {
                            firstIntersect[0] = thirdIntersect[0] + 2 * result[0];
                            firstIntersect[1] = thirdIntersect[1] + 2 * result[1];
                        }
                    } else if (!foundSecondIntersect) {
                        double dx = fourthIntersect[0] + result[0] - bridgeStreet.getStartNode().getX();
                        double dy = fourthIntersect[1] + result[1] - bridgeStreet.getStartNode().getY();
                        double lengthSquared = dx * dx + dy * dy;
                        dx = fourthIntersect[0] + result[0] - bridgeStreet.getEndNode().getX();
                        dy = fourthIntersect[1] + result[1] - bridgeStreet.getEndNode().getY();
                        if (lengthSquared < width * width * 4) {
                            secondIntersect[0] = bridgeStreet.getStartNode().getX() + result[0];
                            secondIntersect[1] = bridgeStreet.getStartNode().getY() + result[1];
                        } else if ((dx * dx + dy * dy) < width * width * 4) {
                            secondIntersect[0] = bridgeStreet.getEndNode().getX() + result[0];
                            secondIntersect[1] = bridgeStreet.getEndNode().getY() + result[1];
                        } else {
                            secondIntersect[0] = fourthIntersect[0] + 2 * result[0];
                            secondIntersect[1] = fourthIntersect[1] + 2 * result[1];
                        }
                    } else if (!foundThirdIntersect) {
                        double dx = firstIntersect[0] - result[0] - bridgeStreet.getStartNode().getX();
                        double dy = firstIntersect[1] - result[1] - bridgeStreet.getStartNode().getY();
                        double lengthSquared = dx * dx + dy * dy;
                        dx = firstIntersect[0] - result[0] - bridgeStreet.getEndNode().getX();
                        dy = firstIntersect[1] - result[1] - bridgeStreet.getEndNode().getY();
                        if (lengthSquared < width * width * 4) {
                            thirdIntersect[0] = bridgeStreet.getStartNode().getX() - result[0];
                            thirdIntersect[1] = bridgeStreet.getStartNode().getY() - result[1];
                        } else if ((dx * dx + dy * dy) < width * width * 4) {
                            thirdIntersect[0] = bridgeStreet.getEndNode().getX() - result[0];
                            thirdIntersect[1] = bridgeStreet.getEndNode().getY() - result[1];
                        } else {
                            thirdIntersect[0] = firstIntersect[0] - 2 * result[0];
                            thirdIntersect[1] = firstIntersect[1] - 2 * result[1];
                        }
                    } else {
                        double dx = secondIntersect[0] - result[0] - bridgeStreet.getStartNode().getX();
                        double dy = secondIntersect[1] - result[1] - bridgeStreet.getStartNode().getY();
                        double lengthSquared = dx * dx + dy * dy;
                        dx = secondIntersect[0] - result[0] - bridgeStreet.getEndNode().getX();
                        dy = secondIntersect[1] - result[1] - bridgeStreet.getEndNode().getY();
                        if (lengthSquared < width * width * 4) {
                            fourthIntersect[0] = bridgeStreet.getStartNode().getX() - result[0];
                            fourthIntersect[1] = bridgeStreet.getStartNode().getY() - result[1];
                        } else if ((dx * dx + dy * dy) < width * width * 4) {
                            fourthIntersect[0] = bridgeStreet.getEndNode().getX() - result[0];
                            fourthIntersect[1] = bridgeStreet.getEndNode().getY() - result[1];
                        } else {
                            fourthIntersect[0] = secondIntersect[0] - 2 * result[0];
                            fourthIntersect[1] = secondIntersect[1] - 2 * result[1];
                        }
                    }
                    intersectsfound = 4;
                }
                if (intersectsfound == 4) {
                    bridgeStreet.addBridgePaintPolygon(firstIntersect[0], firstIntersect[1], secondIntersect[0], secondIntersect[1], thirdIntersect[0], thirdIntersect[1], fourthIntersect[0], fourthIntersect[1]);
                } else {
                    if (intersectsfound == 1) {
                        if (foundFirstIntersect) {
                            thirdIntersect[0] = firstIntersect[0] - 2 * result[0];
                            thirdIntersect[1] = firstIntersect[1] - 2 * result[1];
                            foundThirdIntersect = true;
                        } else if (foundSecondIntersect) {
                            fourthIntersect[0] = secondIntersect[0] - 2 * result[0];
                            fourthIntersect[1] = secondIntersect[1] - 2 * result[1];
                            foundFourthIntersect = true;
                        } else if (foundThirdIntersect) {
                            firstIntersect[0] = thirdIntersect[0] + 2 * result[0];
                            firstIntersect[1] = thirdIntersect[1] + 2 * result[1];
                            foundFirstIntersect = true;
                        } else {
                            secondIntersect[0] = fourthIntersect[0] + 2 * result[0];
                            secondIntersect[1] = fourthIntersect[1] + 2 * result[1];
                            foundSecondIntersect = true;
                        }
                        intersectsfound = 2;
                    }
                    if (intersectsfound == 2) {
                        if (foundFirstIntersect && foundSecondIntersect) {
                            bridgeStreet.addBridgePaintLine(firstIntersect[0], firstIntersect[1], secondIntersect[0], secondIntersect[1]);
                        } else if (foundThirdIntersect && foundFourthIntersect) {
                            bridgeStreet.addBridgePaintLine(thirdIntersect[0], thirdIntersect[1], fourthIntersect[0], fourthIntersect[1]);
                        } else {
                            Street[] crossingStreets = otherStreet.getStartNode().getCrossingStreets();
                            boolean isNearCrossing = false;
                            for (int i = 0; i < crossingStreets.length; ++i) {
                                if (crossingStreets[i].getStartNode() == bridgeStreet.getStartNode() || crossingStreets[i].getStartNode() == bridgeStreet.getEndNode() || crossingStreets[i].getEndNode() == bridgeStreet.getStartNode() || crossingStreets[i].getEndNode() == bridgeStreet.getEndNode()) {
                                    isNearCrossing = true;
                                    break;
                                }
                            }
                            if (!isNearCrossing) {
                                crossingStreets = otherStreet.getEndNode().getCrossingStreets();
                                for (int i = 0; i < crossingStreets.length; ++i) {
                                    if (crossingStreets[i].getStartNode() == bridgeStreet.getStartNode() || crossingStreets[i].getStartNode() == bridgeStreet.getEndNode() || crossingStreets[i].getEndNode() == bridgeStreet.getStartNode() || crossingStreets[i].getEndNode() == bridgeStreet.getEndNode()) {
                                        isNearCrossing = true;
                                        break;
                                    }
                                }
                            }
                            if (!isNearCrossing) {
                                if ((foundFirstIntersect && foundThirdIntersect) || (foundSecondIntersect && foundFourthIntersect)) {
                                    if (foundSecondIntersect && foundFourthIntersect) {
                                        firstIntersect[0] = secondIntersect[0];
                                        firstIntersect[1] = secondIntersect[1];
                                        thirdIntersect[0] = fourthIntersect[0];
                                        thirdIntersect[1] = fourthIntersect[1];
                                    }
                                    double middleX = (thirdIntersect[0] - firstIntersect[0]) / 2 + firstIntersect[0];
                                    double middleY = (thirdIntersect[1] - firstIntersect[1]) / 2 + firstIntersect[1];
                                    double dx = middleX - bridgeStreet.getStartNode().getX();
                                    double dy = middleY - bridgeStreet.getStartNode().getY();
                                    double lengthSquared = dx * dx + dy * dy;
                                    dx = middleX - bridgeStreet.getEndNode().getX();
                                    dy = middleY - bridgeStreet.getEndNode().getY();
                                    if (lengthSquared < (dx * dx + dy * dy)) {
                                        if (bridgeStreet.getStartNode().getCrossingStreetsCount() < 3)
                                            bridgeStreet.addBridgePaintPolygon(firstIntersect[0], firstIntersect[1], bridgeStreet.getStartNode().getX() + result[0], bridgeStreet.getStartNode().getY() + result[1], thirdIntersect[0], thirdIntersect[1], bridgeStreet.getStartNode().getX() - result[0], bridgeStreet.getStartNode().getY() - result[1]);
                                    } else {
                                        if (bridgeStreet.getEndNode().getCrossingStreetsCount() < 3)
                                            bridgeStreet.addBridgePaintPolygon(firstIntersect[0], firstIntersect[1], bridgeStreet.getEndNode().getX() + result[0], bridgeStreet.getEndNode().getY() + result[1], thirdIntersect[0], thirdIntersect[1], bridgeStreet.getEndNode().getX() - result[0], bridgeStreet.getEndNode().getY() - result[1]);
                                    }
                                } else {
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void calculateResizedLine(int[] startPoint, int[] endPoint, double correction, boolean correctStart, boolean correctEnd) {
        if (startPoint.length == 2 && endPoint.length == 2) {
            if (startPoint[0] == endPoint[0]) {
                if (startPoint[1] > endPoint[1]) {
                    startPoint[1] -= correction;
                    endPoint[1] += +correction;
                } else {
                    startPoint[1] += correction;
                    endPoint[1] -= correction;
                }
            } else if (startPoint[1] == endPoint[1]) {
                if (startPoint[0] > endPoint[0]) {
                    startPoint[0] -= correction;
                    endPoint[0] += correction;
                } else {
                    startPoint[0] += correction;
                    endPoint[0] -= correction;
                }
            } else {
                double a = ((double) startPoint[1] - endPoint[1]) / ((double) startPoint[0] - endPoint[0]);
                double b = startPoint[1] - a * startPoint[0];
                if (startPoint[0] > endPoint[0]) {
                    if (correctStart)
                        startPoint[0] -= (int) Math.round(Math.sqrt(correction * correction / (1 + a * a)));
                    if (correctEnd)
                        endPoint[0] += (int) Math.round(Math.sqrt(correction * correction / (1 + a * a)));
                } else {
                    if (correctStart)
                        startPoint[0] += (int) Math.round(Math.sqrt(correction * correction / (1 + a * a)));
                    if (correctEnd)
                        endPoint[0] -= (int) Math.round(Math.sqrt(correction * correction / (1 + a * a)));
                }
                if (correctStart)
                    startPoint[1] = (int) Math.round(a * startPoint[0] + b);
                if (correctEnd)
                    endPoint[1] = (int) Math.round(a * endPoint[0] + b);
            }
        }
    }
}
