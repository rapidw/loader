import React, {useEffect, useReducer, useRef, useState} from "react";
import {Card, Col, Divider, Row, Statistic, Tabs, Typography, Descriptions, Badge} from "antd";
import {PageHeaderWrapper} from '@ant-design/pro-layout';
import ReactJson from "react-json-view";
import {LazyLog} from "react-lazylog";
import {Axis, Chart, Geom, Tooltip, Legend } from "bizcharts";
import set = Reflect.set;


const {TabPane} = Tabs;
const {Title} = Typography;

const Dashboard: React.FC = () => {
  const url = 'https://gist.githubusercontent.com/helfi92/96d4444aa0ed46c5f9060a789d316100/raw/ba0d30a9877ea5cc23c7afcd44505dbc2bab1538/typical-live_backing.log';

  const [masterOptions, setMasterOptions] = useState<any>({"test": 123, "test1": "value"})

  const data = [
    {
      year: "1991",
      value: 3
    },
    {
      year: "1992",
      value: 4
    },
    {
      year: "1993",
      value: 3.5
    },
    {
      year: "1994",
      value: 5
    },
    {
      year: "1995",
      value: 4.9
    },
    {
      year: "1996",
      value: 6
    },
    {
      year: "1997",
      value: 7
    },
    {
      year: "1998",
      value: 9
    },
    {
      year: "1999",
      value: 13
    }
  ];
  const cols = {
    value: {
      min: 0
    },
    year: {
      range: [0, 1]
    }
  };

  const scale = {
    time: {
      alias: "时间",
      type: "time",
      mask: "MM:ss",
      tickCount: 10,
      nice: false
    },
    temperature: {
      alias: "平均温度(°C)",
      min: 10,
      max: 35
    },
    type: {
      type: "cat"
    }
  };

  // const [data2, setData2] = useState<any>([]);

  const red = (state, action) => {
    switch (action.type) {
      case "incr":
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
          temperature: temperature1,
          type: "记录1"
        });
        newdata.push({
          time: time,
          temperature: temperature2,
          type: "记录2"
        });
        return newdata;
    }
  };

  const [data2, dispatch] = useReducer(red, []);


  const call = (id:any) => {
    console.log("called")
    clearInterval(id)
  };

  useEffect(() => {
    setInterval(() => {
      dispatch({type: "incr"})
    }, 1000);
  }, []);

  return (
    <PageHeaderWrapper>
      <Card>
        <div className="site-statistic-demo-card">
          {/*<Typography>*/}
          {/*  <Title level={3}>Agent Status</Title>*/}
          <Row gutter={16}>
            <Col span={4}>
              <Card>
                <Statistic
                  title="Agent Total"
                  value={20}
                />
              </Card>
            </Col>
            <Col span={4}>
              <Card>
                <Statistic
                  title="Agent Ready"
                  value={20}
                />
              </Card>
            </Col>
            <Col span={4}>
              <Card>
                <Statistic
                  title="Agent Used"
                  value={10}
                />
              </Card>
            </Col>
            <Col span={4}>
              <Card>
                <Statistic
                  title="Agent Errored"
                  value={2}
                  valueStyle={{color: '#cf1322'}}
                />
              </Card>
            </Col>
            <Col span={4}>
              <Card>
                <Statistic
                  title="Agent Running"
                  value={2}
                  valueStyle={{color: '#3f8600'}}
                />
              </Card>
            </Col>
            <Col span={4}>
              <Card>
                <Statistic
                  title="Agent Idle"
                  value={2}
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
          <Descriptions.Item label="Product">Cloud Database</Descriptions.Item>
          <Descriptions.Item label="Billing Mode">Prepaid</Descriptions.Item>
          <Descriptions.Item label="Automatic Renewal">YES</Descriptions.Item>
          <Descriptions.Item label="Order time">2018-04-24 18:00:00</Descriptions.Item>
          <Descriptions.Item label="Usage Time" span={2}>
            2019-04-24 18:00:00
          </Descriptions.Item>
          <Descriptions.Item label="Status" span={3}>
            <Badge status="processing" text="Running" />
          </Descriptions.Item>
          <Descriptions.Item label="Negotiated Amount">$80.00</Descriptions.Item>
          <Descriptions.Item label="Discount">$20.00</Descriptions.Item>
          <Descriptions.Item label="Official Receipts">$60.00</Descriptions.Item>
          <Descriptions.Item label="Config Info">
            Data disk type: MongoDB
            <br />
            Database version: 3.4
            <br />
            Package: dds.mongo.mid
            <br />
            Storage space: 10 GB
            <br />
            Replication factor: 3
            <br />
            Region: East China 1<br />
          </Descriptions.Item>
        </Descriptions>

        <Divider/>
        <Typography>
          <Title level={3}>
            Throughput
          </Title>
        </Typography>

        {/*<Chart height={400} data={data} scale={cols} forceFit>*/}
        {/*  <Axis name="year"/>*/}
        {/*  <Axis name="value"/>*/}
        {/*  <Tooltip*/}
        {/*    crosshairs={{*/}
        {/*      type: "y"*/}
        {/*    }}*/}
        {/*  />*/}
        {/*  <Geom type="line" position="year*value" size={2}/>*/}
        {/*  <Geom*/}
        {/*    type="point"*/}
        {/*    position="year*value"*/}
        {/*    size={4}*/}
        {/*    shape={"circle"}*/}
        {/*    style={{*/}
        {/*      stroke: "#fff",*/}
        {/*      lineWidth: 1*/}
        {/*    }}*/}
        {/*  />*/}
        {/*</Chart>*/}

        <Divider/>
        <Typography>
          <Title level={3}>
            Average Response Time
          </Title>
        </Typography>

        {/*<Chart height={400} data={data} scale={cols} forceFit>*/}
        {/*  <Axis name="year"/>*/}
        {/*  <Axis name="value"/>*/}
        {/*  <Tooltip*/}
        {/*    crosshairs={{*/}
        {/*      type: "y"*/}
        {/*    }}*/}
        {/*  />*/}
        {/*  <Geom type="line" position="year*value" size={2}/>*/}
        {/*  <Geom*/}
        {/*    type="point"*/}
        {/*    position="year*value"*/}
        {/*    size={4}*/}
        {/*    shape={"circle"}*/}
        {/*    style={{*/}
        {/*      stroke: "#fff",*/}
        {/*      lineWidth: 1*/}
        {/*    }}*/}
        {/*  />*/}
        {/*</Chart>*/}

        <Divider/>
        <Typography>
          <Title level={3}>
            Master Log
          </Title>
        </Typography>

        <div style={{height: "800px"}}>
          <LazyLog extraLines={1} enableSearch url={url} caseInsensitive rowHeight={24}/>
        </div>

        <Chart
          data={data2}
          scale={scale}
          forceFit
          height={400}
          // onGetG2Instance={g2Chart => {
          //   chart = g2Chart;
          // }}
        >
          <Tooltip />
          {/*{data2.length !== 0 ? <Axis /> : ''}*/}
          <Legend />
          <Geom
            type="line"
            position="time*temperature"
            color={["type", ["#ff7f0e", "#2ca02c"]]}
            shape="smooth"
            size={2}
          />
        </Chart>
      </Card>
    </PageHeaderWrapper>
  )
}

export default Dashboard;
