import React, {useEffect, useReducer, useRef, useState} from "react";
import {Badge, Card, Col, Collapse, Descriptions, Divider, Row, Statistic, Tabs, Typography} from "antd";
import {PageHeaderWrapper} from '@ant-design/pro-layout';
import {LazyLog} from "react-lazylog";
import {Axis, Chart, Geom, Legend, Tooltip} from "bizcharts";
import ReactJson from 'react-json-view';
import useDimensions from "react-use-dimensions";
import moment from "moment";
// import useComponentSize from '@rehooks/component-size'



const {Panel} = Collapse;
const {Title} = Typography;

const Dashboard: React.FC = () => {
  const url = 'https://gist.githubusercontent.com/helfi92/96d4444aa0ed46c5f9060a789d316100/raw/ba0d30a9877ea5cc23c7afcd44505dbc2bab1538/typical-live_backing.log';

  const [taskStatus, setTaskStatus] = useState({status: "processing", text: "IDLE"})

  const startTime = 1583596696795;
  const finishTime = 1583596717807;
  const timeFormat = "YYYY-MM-DD HH:mm:ss";

  const throughputScale = {
    time: {
      alias: "Time",
      type: "time",
      mask: "MM:ss",
      tickCount: 10,
      nice: false
    },
    qps: {
      alias: "QPS",
      type: "linear",
      min: 10,
      max: 35
    },
    type: {
      type: "cat"
    }
  };

  const artScale = {
    time: {
      alias: "Time",
      type: "time",
      mask: "MM:ss",
      tickCount: 10,
      nice: false
    },
    art: {
      alias: "ms",
      type: "linear",
      min: 10,
      max: 35
    },
  };

  const red = (state: any, action: any) => {
    switch (action.type) {
      case "incrThrough":
        let now = new Date();
        let time = now.getTime();
        let temperature1 = ~~(Math.random() * 5) + 22;
        let temperature2 = ~~(Math.random() * 7) + 17;

        // console.log("begin " + now);
        // console.log(data2);
        let newdata = [...state];
        // console.log(moredata);
        if (newdata.length >= 200) {
          newdata.shift();
          newdata.shift();
        }

        newdata.push({
          time: time,
          qps: temperature1,
          type: "Total",
        });
        newdata.push({
          time: time,
          qps: temperature2,
          type: "Error",
        });
        return newdata;
      case "incrArt":
        let now1 = new Date();
        let time1 = now1.getTime();
        let art1 = ~~(Math.random() * 5) + 22;

        let newdata1 = [...state];
        if (newdata1.length >= 200) {
          newdata1.shift();
        }

        newdata1.push({
          time: time1,
          art: art1,
          type: "Total",
        });
        return newdata1;
    }
  };

  const [data2, dispatch] = useReducer(red, []);
  const [artData, artDispatch] = useReducer(red, []);


  const json = {
    string: "this is a test ...",
    integer: 42,
    array: [1, 2, 3, 4, NaN],
    float: 3.14159,
    undefined: undefined,
    object: {
      "first-child": true,
      "second-child": false,
      "last-child": null,
    },
    "string_number": "1234",
    "date": new Date()
  };

  const json2 = {
    string: "this is a test ...",
  };

  useEffect(() => {
    setInterval(() => {
      dispatch({type: "incrThrough"})
    }, 1000);
  }, []);

  useEffect(() => {
    setInterval(() => {
      artDispatch({type: "incrArt"})
    }, 1000);
  }, []);

  return (
    <PageHeaderWrapper>
      <Card>
        <div className="site-statistic-demo-card">
          <Row gutter={16}>
            <Col span={4}>
              <Card>
                <Statistic
                  title="Total Agents"
                  value={20}
                  valueStyle={{fontWeight: "bold"}}
                />
              </Card>
            </Col>
            <Col span={4}>
              <Card>
                <Statistic
                  title="Ready Agents"
                  value={20}
                  valueStyle={{fontWeight: "bold"}}
                />
              </Card>
            </Col>
            <Col span={4}>
              <Card>
                <Statistic
                  title="Occupied Agents"
                  value={10}
                  valueStyle={{color: "#1890ff", fontWeight: "bold"}}
                />
              </Card>
            </Col>
            <Col span={4}>
              <Card>
                <Statistic
                  title="Errored Agents"
                  value={2}
                  valueStyle={{color: '#f5222d', fontWeight: "bold"}}
                />
              </Card>
            </Col>
            <Col span={4}>
              <Card>
                <Statistic
                  title=" Running Agents"
                  value={2}
                  valueStyle={{color: '#52c41a', fontWeight: "bold"}}
                />
              </Card>
            </Col>
            <Col span={4}>
              <Card>
                <Statistic
                  title="Idle Agents"
                  value={2}
                  valueStyle={{fontWeight: "bold"}}
                />
              </Card>
            </Col>
          </Row>
          {/*</Typography>*/}
        </div>

        <Divider/>
        <Typography>
          <Title level={3}>
            Task Info
          </Title>
        </Typography>
        <Descriptions bordered>
          <Descriptions.Item label="Running">
            <Badge status={taskStatus.status} text={taskStatus.text}/>
          </Descriptions.Item>
          <Descriptions.Item label="Start Time">{moment(startTime).format(timeFormat)}</Descriptions.Item>
          <Descriptions.Item label="Finish Time">{moment(finishTime).format(timeFormat)}</Descriptions.Item>
          <Descriptions.Item label="Max Throughput(RPS)">Cloud Database</Descriptions.Item>
          <Descriptions.Item label="Min Throughput(RPS)">Cloud Database</Descriptions.Item>
          <Descriptions.Item label="Throughput P99">Cloud Database</Descriptions.Item>
          <Descriptions.Item label="Response Time Max(ms)">Cloud Database</Descriptions.Item>
          <Descriptions.Item label="Response Time Min(ms)">Cloud Database</Descriptions.Item>
          <Descriptions.Item label="Response Time P99(ms)">Cloud Database</Descriptions.Item>

        </Descriptions>

        <Divider/>
        <Typography>
          <Title level={3}>
            Throughput
          </Title>
        </Typography>

        <Chart height={400} data={data2} scale={throughputScale} forceFit>
          <Axis name="qps" title/>
          <Tooltip
            // crosshairs={{
            //   type: "y"
            // }}
          />
          <Legend />
          <Geom type="line" position="time*qps" size={2} shape="smooth"
                color={["type", ["#1890ff", "#f5222d"]]}/>
          <Geom
            type="point"
            position="time*qps"
            size={4}
            shape={"circle"}
            style={["type", {
              // stroke: "#fff",
              fill: (type: any) => {
                if (type === 'Total')
                  return "#1890ff";
                return "#f5222d";
              },
              lineWidth: 1
            }]}
          />
        </Chart>

        <Divider/>
        <Typography>
          <Title level={3}>
            Average Response Time
          </Title>
        </Typography>

        <Chart height={400} data={artData} scale={artScale} forceFit>
          <Axis name={"art"} title/>
          <Tooltip
            // crosshairs={{
            //   type: "y"
            // }}
          />
          <Legend />
          <Geom type="line" position="time*art" size={2} shape="smooth"
                color="#1890ff"/>
          <Geom
            type="point"
            position="time*art"
            size={4}
            shape={"circle"}
            style={{
              fill: "#1890ff",
              lineWidth: 1
            }}
          />
        </Chart>

        <Divider/>
        <Typography>
          <Title level={3}>
            Master Log
          </Title>
        </Typography>

        <div style={{height: "800px"}}>
          <LazyLog extraLines={1} enableSearch url={url} caseInsensitive rowHeight={24}/>
        </div>

        <Divider/>
        <Typography>
          <Title level={3}>
            Options & Config
          </Title>
        </Typography>
        <Collapse defaultActiveKey="1">

          <Panel header="Master Options" key="1" >
            <ReactJson src={json} name={false} enableClipboard={false}
                       displayDataTypes={false}/>
          </Panel>
          <Panel header="Agent Options" key="2">

            <ReactJson src={json2} name={false} enableClipboard={false}
                       displayDataTypes={false}/>
          </Panel>
          <Panel header="Agent Config" key="3">
            <a href="/">Download</a>
          </Panel>
        </Collapse>
      </Card>
    </PageHeaderWrapper>
  )
};

export default Dashboard;
